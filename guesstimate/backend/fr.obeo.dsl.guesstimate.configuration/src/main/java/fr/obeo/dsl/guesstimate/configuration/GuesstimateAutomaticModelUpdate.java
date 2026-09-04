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
package fr.obeo.dsl.guesstimate.configuration;

import org.eclipse.sirius.components.collaborative.api.ChangeDescription;
import org.eclipse.sirius.components.collaborative.editingcontext.api.IChangeDescriptionConsumer;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextProcessor;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Sinks;

/**
 * Installs the adapter responsible for updating derived Guesstimate model data.
 *
 * @author cedric
 */
@Service
public class GuesstimateAutomaticModelUpdate implements IEditingContextProcessor, IChangeDescriptionConsumer {

    @Override
    public void preProcess(IEditingContext editingContext) {

    }

    @Override
    public void postProcess(IEditingContext editingContext) {
        if (editingContext instanceof IEMFEditingContext siriusWebEditingContext) {
            var resourceSet = siriusWebEditingContext.getDomain().getResourceSet();
            var updateAdapter = resourceSet.eAdapters().stream()
                    .filter(GuesstimateUpdateModelAdapter.class::isInstance)
                    .map(GuesstimateUpdateModelAdapter.class::cast)
                    .findFirst()
                    .orElseGet(() -> {
                        var newUpdateAdapter = new GuesstimateUpdateModelAdapter();
                        resourceSet.eAdapters().add(newUpdateAdapter);
                        return newUpdateAdapter;
                    });
            updateAdapter.resampleDirtySheets();
        }
    }

    @Override
    public void preAccept(Sinks.Many<IPayload> payloadSink, Sinks.Many<Boolean> canBeDisposedSink, IEditingContext editingContext, ChangeDescription changeDescription) {
        if (editingContext instanceof IEMFEditingContext siriusWebEditingContext) {
            siriusWebEditingContext.getDomain().getResourceSet().eAdapters().stream()
                    .filter(GuesstimateUpdateModelAdapter.class::isInstance)
                    .map(GuesstimateUpdateModelAdapter.class::cast)
                    .findFirst()
                    .ifPresent(GuesstimateUpdateModelAdapter::resampleDirtySheets);
        }
    }
}
