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

import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.sirius.components.collaborative.api.ChangeDescription;
import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.components.collaborative.api.IEditingContextEventHandler;
import org.eclipse.sirius.components.core.api.ErrorPayload;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IInput;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.core.api.SuccessPayload;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.emfjson.resource.JsonResourceImpl;
import org.eclipse.sirius.web.domain.services.api.IMessageService;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Sinks.Many;
import reactor.core.publisher.Sinks.One;

/**
 * Replaces one document resource inside the collaborative editing context.
 */
@Service
public class ReplaceDocumentEventHandler implements IEditingContextEventHandler {

    private final IMessageService messageService;

    public ReplaceDocumentEventHandler(IMessageService messageService) {
        this.messageService = java.util.Objects.requireNonNull(messageService);
    }

    @Override
    public boolean canHandle(IEditingContext editingContext, IInput input) {
        return editingContext instanceof IEMFEditingContext && input instanceof ReplaceResourceContentInput;
    }

    @Override
    public void handle(One<IPayload> payloadSink, Many<ChangeDescription> changeDescriptionSink, IEditingContext editingContext, IInput input) {
        IPayload payload = new ErrorPayload(input.id(), this.messageService.unexpectedError());
        ChangeDescription changeDescription = new ChangeDescription(ChangeKind.NOTHING, editingContext.getId(), input);

        if (editingContext instanceof IEMFEditingContext emfEditingContext && input instanceof ReplaceResourceContentInput replaceInput) {
            Resource newResource = replaceInput.newResourceContent();
            var optionalTargetResource = emfEditingContext.getDomain().getResourceSet().getResources().stream()
                    .filter(resource -> resource.getURI().equals(newResource.getURI()))
                    .findFirst();
            if (optionalTargetResource.isPresent()) {
                this.replaceContents(newResource, optionalTargetResource.get());
                payload = new SuccessPayload(input.id());
                changeDescription = new ChangeDescription(ChangeKind.SEMANTIC_CHANGE, editingContext.getId(), input);
            }
        }

        payloadSink.tryEmitValue(payload);
        changeDescriptionSink.tryEmitNext(changeDescription);
    }

    private void replaceContents(Resource newResource, Resource targetResource) {
        Map<EObject, String> ids = new IdentityHashMap<>();
        for (EObject root : newResource.getContents()) {
            this.collectIds(newResource, root, ids);
            root.eAllContents().forEachRemaining(object -> this.collectIds(newResource, object, ids));
        }

        targetResource.getContents().clear();
        targetResource.getContents().addAll(newResource.getContents());
        if (targetResource instanceof JsonResourceImpl jsonResource) {
            ids.forEach(jsonResource::setID);
        }
    }

    private void collectIds(Resource resource, EObject object, Map<EObject, String> ids) {
        String id = resource instanceof XMLResource xmlResource ? xmlResource.getID(object) : EcoreUtil.getID(object);
        if (id != null) {
            ids.put(object, id);
        }
    }
}
