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
package org.eclipse.sirius.web.restfulemf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.sirius.components.collaborative.api.ChangeDescription;
import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.components.collaborative.api.IEditingContextEventHandler;
import org.eclipse.sirius.components.collaborative.api.Monitoring;
import org.eclipse.sirius.components.core.api.ErrorPayload;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IInput;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.emfjson.resource.PackageNotFoundError;
import org.eclipse.sirius.web.restfulemf.application.RestfulEMFPersistenceService;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceWriteStatus;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.services.ResourcePaths;
import org.eclipse.sirius.web.restfulemf.services.api.IResourceFormatService;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Sinks.Many;
import reactor.core.publisher.Sinks.One;

/**
 * Replaces one document resource inside the collaborative editing context.
 *
 * @author cbrun
 */
@Service
public class ReplaceDocumentEventHandler implements IEditingContextEventHandler {

    private final IResourceSnapshotService resourceSnapshotService;

    private final IResourceFormatService resourceFormatService;

    private final ResourcePaths resourcePaths;

    private final RestfulEMFPersistenceService persistenceService;

    private final Counter counter;

    private final Logger logger = LoggerFactory.getLogger(ReplaceDocumentEventHandler.class);

    public ReplaceDocumentEventHandler(IResourceSnapshotService resourceSnapshotService, IResourceFormatService resourceFormatService,
            ResourcePaths resourcePaths, RestfulEMFPersistenceService persistenceService, MeterRegistry meterRegistry) {
        this.resourceSnapshotService = Objects.requireNonNull(resourceSnapshotService);
        this.resourceFormatService = Objects.requireNonNull(resourceFormatService);
        this.resourcePaths = Objects.requireNonNull(resourcePaths);
        this.persistenceService = Objects.requireNonNull(persistenceService);
        this.counter = Counter.builder(Monitoring.EVENT_HANDLER)
                .tag(Monitoring.NAME, this.getClass().getSimpleName())
                .register(meterRegistry);
    }

    @Override
    public boolean canHandle(IEditingContext editingContext, IInput input) {
        return editingContext instanceof IEMFEditingContext && input instanceof ReplaceResourceContentInput;
    }

    @Override
    // Unchecked model or persistence failures must complete the response sink and suppress semantic-change publication.
    @SuppressWarnings("checkstyle:IllegalCatch")
    public void handle(One<IPayload> payloadSink, Many<ChangeDescription> changeDescriptionSink, IEditingContext editingContext, IInput input) {
        this.counter.increment();

        IPayload payload = new ErrorPayload(input.id(), "Unexpected error");
        ChangeDescription changeDescription = new ChangeDescription(ChangeKind.NOTHING, editingContext.getId(), input);

        if (editingContext instanceof IEMFEditingContext emfEditingContext && input instanceof ReplaceResourceContentInput replaceInput) {
            try {
                payload = this.write(emfEditingContext, replaceInput);
                if (payload instanceof ReplaceResourceContentSuccessPayload) {
                    changeDescription = new ChangeDescription(ChangeKind.SEMANTIC_CHANGE, editingContext.getId(), input);
                }
            } catch (RuntimeException exception) {
                this.logger.atWarn().setMessage("REST EMF write failed").addKeyValue("editingContextId", editingContext.getId()).setCause(exception).log();
                payloadSink.tryEmitError(exception);
                changeDescriptionSink.tryEmitNext(changeDescription);
                return;
            }
        }

        payloadSink.tryEmitValue(payload);
        changeDescriptionSink.tryEmitNext(changeDescription);
    }

    private IPayload write(IEMFEditingContext editingContext, ReplaceResourceContentInput input) {
        var resourceSet = editingContext.getDomain().getResourceSet();
        var documents = this.resourcePaths.documents(resourceSet);
        var optionalDocument = this.resourcePaths.find(documents, input.path());
        boolean created = optionalDocument.isEmpty();
        if (created && (input.path().equals("_by-id") || input.path().startsWith("_by-id/"))) {
            throw new RestfulEMFException(RestfulEMFError.NOT_FOUND, "Reserved document paths cannot be created");
        }
        var document = optionalDocument.orElseGet(() -> new ResourceDocument(UUID.randomUUID(), input.path(), input.path(), false));
        if (document.readOnly()) {
            throw new RestfulEMFException(RestfulEMFError.READ_ONLY, "The document is read-only");
        }
        var factory = new JSONResourceFactory();
        var uri = factory.createResourceURI(document.id().toString());
        Resource target;
        if (created) {
            target = factory.createResource(uri);
        } else {
            target = resourceSet.getResource(uri, false);
        }
        String revision = "";
        if (!created && !input.expectedRevisions().isEmpty()) {
            revision = this.resourceFormatService.representationRevision(this.snapshot(target), document, documents, input.format(), ",");
        }
        boolean conflict = input.createOnly() && !created;
        if (!input.expectedRevisions().isEmpty()) {
            boolean matches = input.expectedRevisions().contains("*") || input.expectedRevisions().contains(revision);
            conflict = conflict || created || !matches;
        }
        if (conflict) {
            return new ResourceRevisionConflictPayload(input.id(), revision);
        }
        var replacement = this.resourceFormatService.deserialize(new ByteArrayInputStream(input.content()), document, input.format(), documents, resourceSet);
        this.apply(editingContext, input, target, replacement, created);
        ResourceWriteStatus status = ResourceWriteStatus.UPDATED;
        if (created) {
            status = ResourceWriteStatus.CREATED;
        }
        return new ReplaceResourceContentSuccessPayload(input.id(), status);
    }

    private ResourceSnapshot snapshot(Resource resource) {
        return this.resourceSnapshotService.getSnapshot(resource)
                .orElseThrow(() -> new IllegalStateException("An EMF resource could not be serialized"));
    }

    // Any unchecked EMF or persistence failure requires rollback; a rollback failure must preserve the original cause.
    @SuppressWarnings("checkstyle:IllegalCatch")
    private void apply(IEMFEditingContext editingContext, ReplaceResourceContentInput input, Resource target, ResourceSnapshot replacement, boolean created) {
        var resourceSet = editingContext.getDomain().getResourceSet();
        String oldContent = "";
        List<ExternalReference> references = List.of();
        if (!created) {
            oldContent = this.snapshot(target).content();
            references = this.getExternalReferences(target);
        }
        Map<InternalEObject, URI> proxyURIs = this.getProxyURIs(resourceSet.getResources());
        List<ProxyReference> proxyReferences = this.getProxyReferences(resourceSet.getResources(), target);
        try {
            if (created) {
                target.eAdapters().add(new ResourceMetadataAdapter(input.path()));
                resourceSet.getResources().add(target);
            }
            this.reload(target, replacement.content());
            references.forEach(reference -> reference.rebind(target));
            this.resourceFormatService.reconcileReferences(resourceSet, this.resourcePaths.documents(resourceSet));
            this.persistenceService.persist(input, editingContext, target);
        } catch (IOException | RuntimeException exception) {
            try {
                if (created) {
                    resourceSet.getResources().remove(target);
                } else {
                    this.reload(target, oldContent);
                }
                proxyReferences.forEach(ProxyReference::restore);
                references.forEach(reference -> reference.rebind(target));
                proxyURIs.forEach(InternalEObject::eSetProxyURI);
            } catch (IOException | RuntimeException rollbackException) {
                exception.addSuppressed(rollbackException);
                this.logger.atError().setMessage("REST EMF write rollback failed").addKeyValue("editingContextId", editingContext.getId()).setCause(exception).log();
            }
            if (exception instanceof RestfulEMFException restfulException) {
                throw restfulException;
            }
            throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "The EMF document could not be persisted", exception);
        }
    }

    private List<ProxyReference> getProxyReferences(List<Resource> resources, Resource target) {
        List<ProxyReference> result = new ArrayList<>();
        resources.stream().filter(resource -> resource != target).forEach(resource ->
                EcoreUtil.<EObject>getAllProperContents(resource, false).forEachRemaining(object -> {
                    if (!object.eIsProxy()) {
                        object.eClass().getEAllReferences().stream().filter(feature -> feature.isChangeable() && !feature.isDerived() && !feature.isContainer())
                                .forEach(feature -> {
                                    var setting = ((InternalEObject) object).eSetting(feature);
                                    if (setting.get(false) instanceof InternalEList<?> list) {
                                        var values = list.basicList();
                                        if (values.stream().anyMatch(value -> value instanceof EObject proxy && proxy.eIsProxy())) {
                                            result.add(new ProxyReference(setting, new ArrayList<>(values)));
                                        }
                                    } else if (setting.get(false) instanceof EObject proxy && proxy.eIsProxy()) {
                                        result.add(new ProxyReference(setting, proxy));
                                    }
                                });
                    }
                }));
        return result;
    }

    private Map<InternalEObject, URI> getProxyURIs(List<Resource> resources) {
        Map<InternalEObject, URI> result = new IdentityHashMap<>();
        for (var resource : List.copyOf(resources)) {
            EcoreUtil.<EObject>getAllProperContents(resource, false).forEachRemaining(object -> {
                if (object.eIsProxy() && object instanceof InternalEObject proxy) {
                    result.put(proxy, proxy.eProxyURI());
                    return;
                }
                var references = ((InternalEList<EObject>) object.eCrossReferences()).basicIterator();
                while (references.hasNext()) {
                    if (references.next() instanceof InternalEObject proxy && proxy.eIsProxy()) {
                        result.put(proxy, proxy.eProxyURI());
                    }
                }
            });
        }
        return result;
    }

    private List<ExternalReference> getExternalReferences(Resource targetResource) {
        Set<EObject> targets = Collections.newSetFromMap(new IdentityHashMap<>());
        EcoreUtil.<EObject>getAllProperContents(targetResource, false).forEachRemaining(object -> {
            if (!object.eIsProxy()) {
                targets.add(object);
            }
        });
        List<ExternalReference> references = new ArrayList<>();
        if (targetResource.getResourceSet() != null) {
            targetResource.getResourceSet().getResources().stream().filter(resource -> resource != targetResource)
                    .forEach(resource -> EcoreUtil.<EObject>getAllProperContents(resource, false).forEachRemaining(object -> {
                        if (!object.eIsProxy()) {
                            object.eClass().getEAllReferences().stream()
                                    .filter(feature -> !feature.isContainment() && !feature.isContainer() && !feature.isDerived() && feature.isChangeable())
                                    .forEach(feature -> this.addExternalReferences(references, ((InternalEObject) object).eSetting(feature), targets, targetResource));
                        }
                    }));
        }
        return references;
    }

    private void addExternalReferences(List<ExternalReference> references, EStructuralFeature.Setting setting, Set<EObject> targets, Resource resource) {
        if (setting.getEStructuralFeature().isMany() && setting.get(false) instanceof InternalEList<?> list) {
            var values = list.basicList();
            for (int index = 0; index < values.size(); index++) {
                if (values.get(index) instanceof EObject target && targets.contains(target)) {
                    references.add(new ExternalReference(setting, index, resource.getURIFragment(target), target.eClass()));
                }
            }
        } else if (setting.get(false) instanceof EObject target && targets.contains(target)) {
            references.add(new ExternalReference(setting, -1, resource.getURIFragment(target), target.eClass()));
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
        // emfjson first tries external document references as package URIs, reports this diagnostic,
        // then correctly creates the proxy. Its diagnostic retains a relative URI even after the proxy URI is resolved.
        // Keep genuine metamodel lookup failures fatal.
        Set<URI> documentReferences = new HashSet<>();
        EcoreUtil.<EObject>getAllProperContents(resource, false).forEachRemaining(object -> {
            var references = ((InternalEList<EObject>) object.eCrossReferences()).basicIterator();
            references.forEachRemaining(reference -> {
                URI uri = EcoreUtil.getURI(reference).trimFragment();
                if (uri.toString().startsWith("restfulemf:/documents/") || IEMFEditingContext.RESOURCE_SCHEME.equals(uri.scheme())) {
                    documentReferences.add(uri);
                }
            });
        });
        resource.getErrors().removeIf(error -> error instanceof PackageNotFoundError missingPackage
                && documentReferences.contains(URI.createURI(missingPackage.getUri()).resolve(resource.getURI()).trimFragment()));
        if (!resource.getErrors().isEmpty()) {
            throw new IOException(resource.getErrors().get(0).getMessage());
        }
    }

    /**
     * A resolved inbound reference whose target instance is replaced when its resource is reloaded.
     */
    private record ExternalReference(EStructuralFeature.Setting setting, int index, String fragment, EClass eClass) {

        private void rebind(Resource resource) {
            EObject target = resource.getEObject(this.fragment);
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

    /**
     * Original proxy slots, retained because JSON serialization can resolve and replace them before a failed commit.
     */
    private record ProxyReference(EStructuralFeature.Setting setting, Object value) {

        private void restore() {
            this.setting.set(this.value);
        }
    }
}
