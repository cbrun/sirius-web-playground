/*******************************************************************************
 * Copyright (c) 2019, 2023 Obeo.
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

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.sirius.components.collaborative.api.ChangeDescription;
import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.components.collaborative.api.IEditingContextEventHandler;
import org.eclipse.sirius.components.core.api.ErrorPayload;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IInput;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.core.api.SuccessPayload;
import org.eclipse.sirius.emfjson.resource.JsonResourceImpl;
import org.eclipse.sirius.web.services.editingcontext.EditingContext;
import org.eclipse.sirius.web.services.messages.IServicesMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Sinks.Many;
import reactor.core.publisher.Sinks.One;

/**
 * Event handler used to create a new document from a file upload.
 * 
 * @author Cedric Brun <cedric.brun@obeo.fr>
 */
@Service
public class ReplaceDocumentEventHandler implements IEditingContextEventHandler {

	private final Logger logger = LoggerFactory.getLogger(ReplaceDocumentEventHandler.class);

	private final IServicesMessageService messageService;

	public ReplaceDocumentEventHandler(IServicesMessageService messageService, MeterRegistry meterRegistry) {
		this.messageService = Objects.requireNonNull(messageService);

	}

	@Override
	public boolean canHandle(IEditingContext editingContext, IInput input) {
		return input instanceof ReplaceResourceContentInput;
	}

	@Override
	public void handle(One<IPayload> payloadSink, Many<ChangeDescription> changeDescriptionSink,
			IEditingContext editingContext, IInput input) {

		IPayload payload = new ErrorPayload(input.id(), this.messageService.unexpectedError());
		ChangeDescription changeDescription = new ChangeDescription(ChangeKind.NOTHING, editingContext.getId(), input);

		if (input instanceof ReplaceResourceContentInput) {

			ReplaceResourceContentInput uploadDocumentInput = (ReplaceResourceContentInput) input;
			Resource newVersionOfResource = uploadDocumentInput.newResourceContent();

			// @formatter:off
            Optional<AdapterFactoryEditingDomain> optionalEditingDomain = Optional.of(editingContext)
                    .filter(EditingContext.class::isInstance)
                    .map(EditingContext.class::cast)
                    .map(EditingContext::getDomain);
            // @formatter:on

			if (optionalEditingDomain.isPresent()) {
				AdapterFactoryEditingDomain adapterFactoryEditingDomain = optionalEditingDomain.get();

				for (Resource r : adapterFactoryEditingDomain.getResourceSet().getResources()) {
					if (newVersionOfResource.getURI().equals(r.getURI())) {
						Map<EObject, String> idsFromNewRes = Maps.newLinkedHashMap();
						Iterator<EObject> it = newVersionOfResource.getAllContents();
						while (it.hasNext()) {
							EObject e = it.next();
							String id = null;
							if (newVersionOfResource instanceof XMLResource) {
								id = ((XMLResource) newVersionOfResource).getID(e);
							} else {
								id = EcoreUtil.getID(e);
							}
							if (id != null) {
								idsFromNewRes.put(e, id);
							}
						}
						r.getContents().clear();
						r.getContents().addAll(newVersionOfResource.getContents());
						if (r instanceof JsonResourceImpl) {
							for (EObject e : idsFromNewRes.keySet()) {
								((JsonResourceImpl) r).setID(e, idsFromNewRes.get(e));
							}
						}
						//
						// mergeUsingCompare(newVersionOfResource, r);

					}
				}
				payload = new SuccessPayload(input.id());
				changeDescription = new ChangeDescription(ChangeKind.SEMANTIC_CHANGE, editingContext.getId(), input);
			} else {
				logger.info("could not find an editing domain");
			}
		}

		payloadSink.tryEmitValue(payload);
		changeDescriptionSink.tryEmitNext(changeDescription);

	}

//    private void mergeUsingCompare(Resource newVersionOfResource, Resource r) {
//        Comparison comparison = this.compare.compare(r, newVersionOfResource);
//        StringBuffer cmp = new StringBuffer();
//        this.compare.printDifferences(comparison, cmp);
//        this.logger.info("detected differences : \n" + cmp.toString());
//        this.compare.merge(comparison);
//    }

}
