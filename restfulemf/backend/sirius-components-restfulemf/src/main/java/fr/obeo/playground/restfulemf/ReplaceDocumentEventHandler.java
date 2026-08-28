/*******************************************************************************
 * Copyright (c) 2019, 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package fr.obeo.playground.restfulemf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.components.collaborative.api.ChangeDescription;
import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.components.collaborative.api.IEditingContextEventHandler;
import org.eclipse.sirius.components.collaborative.api.Monitoring;
import org.eclipse.sirius.components.core.api.ErrorPayload;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IInput;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Sinks.Many;
import reactor.core.publisher.Sinks.One;

/**
 * Replaces one document resource inside the collaborative editing context.
 */
@Service
public class ReplaceDocumentEventHandler implements IEditingContextEventHandler {

    private final IResourceSnapshotService resourceSnapshotService;

    private final Counter counter;

    private final Logger logger = LoggerFactory.getLogger(ReplaceDocumentEventHandler.class);

    public ReplaceDocumentEventHandler(IResourceSnapshotService resourceSnapshotService, MeterRegistry meterRegistry) {
        this.resourceSnapshotService = Objects.requireNonNull(resourceSnapshotService);
        this.counter = Counter.builder(Monitoring.EVENT_HANDLER)
                .tag(Monitoring.NAME, this.getClass().getSimpleName())
                .register(meterRegistry);
    }

    @Override
    public boolean canHandle(IEditingContext editingContext, IInput input) {
        return editingContext instanceof IEMFEditingContext && input instanceof ReplaceResourceContentInput;
    }

    @Override
    public void handle(One<IPayload> payloadSink, Many<ChangeDescription> changeDescriptionSink, IEditingContext editingContext, IInput input) {
        this.counter.increment();

        IPayload payload = new ErrorPayload(input.id(), "Unexpected error");
        ChangeDescription changeDescription = new ChangeDescription(ChangeKind.NOTHING, editingContext.getId(), input);

        if (editingContext instanceof IEMFEditingContext emfEditingContext && input instanceof ReplaceResourceContentInput replaceInput) {
            var resourceURI = new JSONResourceFactory().createResourceURI(replaceInput.documentId());
            var optionalTargetResource = emfEditingContext.getDomain().getResourceSet().getResources().stream()
                    .filter(resource -> resource.getURI().equals(resourceURI))
                    .findFirst();
            if (optionalTargetResource.isPresent()) {
                Resource targetResource = optionalTargetResource.get();
                var optionalCurrentSnapshot = this.resourceSnapshotService.getSnapshot(targetResource);
                if (optionalCurrentSnapshot.isPresent()) {
                    ResourceSnapshot currentSnapshot = optionalCurrentSnapshot.get();
                    if (!this.matches(replaceInput.expectedRevisions(), currentSnapshot.revision())) {
                        payload = new ResourceRevisionConflictPayload(input.id(), currentSnapshot.revision());
                    } else if (currentSnapshot.content().equals(replaceInput.newResourceContent())) {
                        payload = new ReplaceResourceContentSuccessPayload(input.id(), currentSnapshot.revision());
                    } else {
                        var optionalNewSnapshot = this.replaceContents(targetResource, replaceInput.newResourceContent(), currentSnapshot,
                                editingContext.getId(), replaceInput.documentId());
                        if (optionalNewSnapshot.isPresent()) {
                            payload = new ReplaceResourceContentSuccessPayload(input.id(), optionalNewSnapshot.get().revision());
                            changeDescription = new ChangeDescription(ChangeKind.SEMANTIC_CHANGE, editingContext.getId(), input);
                        }
                    }
                } else {
                    this.logFailure(editingContext.getId(), replaceInput.documentId());
                }
            } else {
                this.logFailure(editingContext.getId(), replaceInput.documentId());
            }
            this.logOutcome(editingContext.getId(), replaceInput.documentId(), payload);
        }

        payloadSink.tryEmitValue(payload);
        changeDescriptionSink.tryEmitNext(changeDescription);
    }

    private void logOutcome(String editingContextId, String documentId, IPayload payload) {
        if (payload instanceof ReplaceResourceContentSuccessPayload) {
            this.logger.atInfo()
                    .setMessage("EMF resource {} replaced")
                    .addArgument(documentId)
                    .addKeyValue("editingContextId", editingContextId)
                    .addKeyValue("documentId", documentId)
                    .log();
        } else if (payload instanceof ResourceRevisionConflictPayload) {
            this.logger.atWarn()
                    .setMessage("Replacement of EMF resource {} rejected due to a revision conflict")
                    .addArgument(documentId)
                    .addKeyValue("editingContextId", editingContextId)
                    .addKeyValue("documentId", documentId)
                    .log();
        }
    }

    private void logFailure(String editingContextId, String documentId) {
        this.logger.atWarn()
                .setMessage("Replacement of EMF resource {} failed")
                .addArgument(documentId)
                .addKeyValue("editingContextId", editingContextId)
                .addKeyValue("documentId", documentId)
                .log();
    }

    private boolean matches(java.util.List<String> expectedRevisions, String currentRevision) {
        return expectedRevisions.isEmpty() || expectedRevisions.contains("*") || expectedRevisions.contains(currentRevision);
    }

    private Optional<ResourceSnapshot> replaceContents(Resource targetResource, String newContent, ResourceSnapshot currentSnapshot, String editingContextId, String documentId) {
        List<ExternalReference> externalReferences = this.getExternalReferences(targetResource);
        try {
            this.reload(targetResource, newContent);
            externalReferences.forEach(reference -> reference.rebind(targetResource));
            return Optional.of(this.resourceSnapshotService.getSnapshot(targetResource)
                    .orElseThrow(() -> new IllegalStateException("The updated EMF resource could not be serialized")));
        } catch (IOException | RuntimeException replacementException) {
            this.logger.atWarn()
                    .setMessage("Replacement of EMF resource {} failed")
                    .addArgument(documentId)
                    .addKeyValue("editingContextId", editingContextId)
                    .addKeyValue("documentId", documentId)
                    .setCause(replacementException)
                    .log();
            try {
                this.reload(targetResource, currentSnapshot.content());
                externalReferences.forEach(reference -> reference.rebind(targetResource));
            } catch (IOException | RuntimeException rollbackException) {
                replacementException.addSuppressed(rollbackException);
                this.logger.atError()
                        .setMessage("Rollback of EMF resource {} failed")
                        .addArgument(targetResource.getURI())
                        .addKeyValue("editingContextId", editingContextId)
                        .addKeyValue("documentId", documentId)
                        .setCause(replacementException)
                        .log();
            }
            return Optional.empty();
        }
    }

    private List<ExternalReference> getExternalReferences(Resource targetResource) {
        List<EObject> targets = new ArrayList<>();
        targetResource.getAllContents().forEachRemaining(targets::add);
        List<ExternalReference> references = new ArrayList<>();
        if (targetResource.getResourceSet() != null) {
            targets.forEach(target -> EcoreUtil.UsageCrossReferencer.find(target, targetResource.getResourceSet()).stream()
                    .filter(setting -> setting.getEObject().eResource() != targetResource)
                    .forEach(setting -> this.addExternalReferences(references, setting, target, targetResource.getURIFragment(target))));
        }
        return references;
    }

    private void addExternalReferences(List<ExternalReference> references, EStructuralFeature.Setting setting, EObject target, String fragment) {
        if (setting.getEStructuralFeature().isMany() && setting.get(false) instanceof List<?> values) {
            for (int index = 0; index < values.size(); index++) {
                if (values.get(index) == target) {
                    references.add(new ExternalReference(setting, index, fragment, target.eClass()));
                }
            }
        } else {
            references.add(new ExternalReference(setting, -1, fragment, target.eClass()));
        }
    }

    private void reload(Resource resource, String content) throws IOException {
        // Sirius can populate an in-memory resource without marking it as loaded. Clearing it first makes unload reset
        // the EMF JSON ID index as well as the usual resource state.
        if (!resource.isLoaded()) {
            resource.getContents().clear();
        }
        resource.unload();
        try (var inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
            resource.load(inputStream, Map.of());
        }
        if (!resource.getErrors().isEmpty()) {
            throw new IOException(resource.getErrors().get(0).getMessage());
        }
    }

    private record ExternalReference(EStructuralFeature.Setting setting, int index, String fragment, EClass eClass) {

        private void rebind(Resource resource) {
            EObject target = null;
            var iterator = resource.getAllContents();
            while (target == null && iterator.hasNext()) {
                EObject candidate = iterator.next();
                if (this.fragment.equals(resource.getURIFragment(candidate))) {
                    target = candidate;
                }
            }
            if (target == null) {
                target = this.eClass.getEPackage().getEFactoryInstance().create(this.eClass);
                ((InternalEObject) target).eSetProxyURI(resource.getURI().appendFragment(this.fragment));
            }
            if (this.index >= 0 && this.setting.get(false) instanceof List<?> values) {
                @SuppressWarnings("unchecked")
                List<Object> writableValues = (List<Object>) values;
                writableValues.set(this.index, target);
            } else {
                this.setting.set(target);
            }
        }
    }
}
