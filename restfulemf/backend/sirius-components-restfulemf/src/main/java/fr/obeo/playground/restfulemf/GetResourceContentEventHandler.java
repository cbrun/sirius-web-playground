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
package fr.obeo.playground.restfulemf;

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

import reactor.core.publisher.Sinks.Many;
import reactor.core.publisher.Sinks.One;

/**
 * Serializes one resource while its collaborative editing context is being processed.
 */
@Service
public class GetResourceContentEventHandler implements IEditingContextEventHandler {

    private final IResourceSnapshotService resourceSnapshotService;

    public GetResourceContentEventHandler(IResourceSnapshotService resourceSnapshotService) {
        this.resourceSnapshotService = Objects.requireNonNull(resourceSnapshotService);
    }

    @Override
    public boolean canHandle(IEditingContext editingContext, IInput input) {
        return editingContext instanceof IEMFEditingContext && input instanceof GetResourceContentInput;
    }

    @Override
    public void handle(One<IPayload> payloadSink, Many<ChangeDescription> changeDescriptionSink, IEditingContext editingContext, IInput input) {
        IPayload payload = new ErrorPayload(input.id(), "Unexpected error");
        if (editingContext instanceof IEMFEditingContext emfEditingContext && input instanceof GetResourceContentInput resourceInput) {
            var resourceURI = new JSONResourceFactory().createResourceURI(resourceInput.documentId());
            payload = emfEditingContext.getDomain().getResourceSet().getResources().stream()
                    .filter(resource -> resourceURI.equals(resource.getURI()))
                    .findFirst()
                    .flatMap(this.resourceSnapshotService::getSnapshot)
                    .<IPayload>map(snapshot -> new GetResourceContentSuccessPayload(input.id(), snapshot))
                    .orElse(payload);
        }

        payloadSink.tryEmitValue(payload);
        changeDescriptionSink.tryEmitNext(new ChangeDescription(ChangeKind.NOTHING, editingContext.getId(), input));
    }
}
