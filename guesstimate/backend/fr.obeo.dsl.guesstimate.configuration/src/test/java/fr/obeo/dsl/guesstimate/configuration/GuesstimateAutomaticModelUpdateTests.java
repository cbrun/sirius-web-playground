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

import static org.assertj.core.api.Assertions.assertThat;

import fr.obeo.dsl.guesstimate.FormulaSetting;
import fr.obeo.dsl.guesstimate.GuesstimateFactory;
import fr.obeo.dsl.guesstimate.NormalDistribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.sirius.components.collaborative.api.ChangeDescription;
import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.components.collaborative.dto.DeleteRepresentationInput;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.web.application.editingcontext.EditingContext;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Sinks;

/**
 * Tests the automatic update of derived Guesstimate model data.
 *
 * @author cedric
 */
public class GuesstimateAutomaticModelUpdateTests {

    @Test
    public void givenSeveralModelNotificationsWhenAcceptingAChangeThenTheSheetIsResampledOnce() {
        var resourceSet = new ResourceSetImpl();
        var resource = new ResourceImpl(URI.createURI("guesstimate:/test"));
        resourceSet.getResources().add(resource);
        var adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
        var editingDomain = new AdapterFactoryEditingDomain(adapterFactory, new BasicCommandStack(), resourceSet);
        var editingContext = new EditingContext("editingContextId", editingDomain, new HashMap<>(), new ArrayList<>());

        var sheet = new CountingSheet();
        var inputVariable = GuesstimateFactory.eINSTANCE.createVariable();
        inputVariable.setName("A");
        inputVariable.setType(fr.obeo.dsl.guesstimate.VariableType.NORMAL);
        var formulaVariable = GuesstimateFactory.eINSTANCE.createVariable();
        formulaVariable.setName("B");
        formulaVariable.setType(fr.obeo.dsl.guesstimate.VariableType.FORMULA);
        sheet.getVariables().add(inputVariable);
        sheet.getVariables().add(formulaVariable);
        resource.getContents().add(sheet);

        var automaticModelUpdate = new GuesstimateAutomaticModelUpdate();
        automaticModelUpdate.postProcess(editingContext);
        assertThat(sheet.getResamplingCount()).isOne();

        ((NormalDistribution) inputVariable.getSettings()).setMean(42);
        ((FormulaSetting) formulaVariable.getSettings()).setFormula("A + 1");
        assertThat(sheet.getResamplingCount()).isOne();

        this.preAccept(automaticModelUpdate, editingContext);
        assertThat(sheet.getResamplingCount()).isEqualTo(2);

        this.preAccept(automaticModelUpdate, editingContext);
        formulaVariable.setDocumentation("No impact on sampling");
        this.preAccept(automaticModelUpdate, editingContext);
        assertThat(sheet.getResamplingCount()).isEqualTo(2);

        editingContext.dispose();
    }

    private void preAccept(GuesstimateAutomaticModelUpdate automaticModelUpdate, EditingContext editingContext) {
        var input = new DeleteRepresentationInput(UUID.randomUUID(), editingContext.getId(), "representationId");
        var changeDescription = new ChangeDescription(ChangeKind.SEMANTIC_CHANGE, "representationId", input);
        Sinks.Many<IPayload> payloadSink = Sinks.many().multicast().directBestEffort();
        Sinks.Many<Boolean> canBeDisposedSink = Sinks.many().multicast().directBestEffort();
        automaticModelUpdate.preAccept(payloadSink, canBeDisposedSink, editingContext, changeDescription);
    }
}
