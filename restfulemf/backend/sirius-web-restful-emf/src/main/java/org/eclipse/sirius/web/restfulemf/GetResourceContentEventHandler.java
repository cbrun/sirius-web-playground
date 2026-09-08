/*******************************************************************************
 * Copyright (c) 2026 Obeo.
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

import java.util.Objects;

import org.eclipse.sirius.components.collaborative.api.ChangeDescription;
import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.components.collaborative.api.IEditingContextEventHandler;
import org.eclipse.sirius.components.core.api.ErrorPayload;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IInput;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.springframework.stereotype.Service;
import org.eclipse.sirius.web.restfulemf.services.ResourcePaths;

import reactor.core.publisher.Sinks.Many;
import reactor.core.publisher.Sinks.One;

/**
 * Serializes one resource while its collaborative editing context is being processed.
 *
 * @author cbrun
 */
@Service
public class GetResourceContentEventHandler implements IEditingContextEventHandler {

    private final IResourceSnapshotService resourceSnapshotService;

    private final ResourcePaths resourcePaths;

    public GetResourceContentEventHandler(IResourceSnapshotService resourceSnapshotService, ResourcePaths resourcePaths) {
        this.resourceSnapshotService = Objects.requireNonNull(resourceSnapshotService);
        this.resourcePaths = Objects.requireNonNull(resourcePaths);
    }

    @Override
    public boolean canHandle(IEditingContext editingContext, IInput input) {
        return editingContext instanceof IEMFEditingContext && input instanceof GetResourceContentInput;
    }

    @Override
    // Unchecked EMF failures must reach the response sink instead of leaving the request without a result.
    @SuppressWarnings("checkstyle:IllegalCatch")
    public void handle(One<IPayload> payloadSink, Many<ChangeDescription> changeDescriptionSink, IEditingContext editingContext, IInput input) {
        try {
            IPayload payload = new ErrorPayload(input.id(), "Unexpected error");
            if (editingContext instanceof IEMFEditingContext emfEditingContext && input instanceof GetResourceContentInput resourceInput) {
                var resourceSet = emfEditingContext.getDomain().getResourceSet();
                var documents = this.resourcePaths.documents(resourceSet);
                var optionalDocument = this.resourcePaths.find(documents, resourceInput.path());
                if (optionalDocument.isPresent()) {
                    var document = optionalDocument.get();
                    var resourceURI = new JSONResourceFactory().createResourceURI(document.id().toString());
                    payload = resourceSet.getResources().stream()
                            .filter(resource -> resourceURI.equals(resource.getURI()))
                            .findFirst()
                            .flatMap(this.resourceSnapshotService::getSnapshot)
                            .<IPayload>map(snapshot -> new GetResourceContentSuccessPayload(input.id(), snapshot, document, documents))
                            .orElse(payload);
                }
            }
            payloadSink.tryEmitValue(payload);
        } catch (RuntimeException exception) {
            payloadSink.tryEmitError(exception);
        }
        changeDescriptionSink.tryEmitNext(new ChangeDescription(ChangeKind.NOTHING, editingContext.getId(), input));
    }

}
