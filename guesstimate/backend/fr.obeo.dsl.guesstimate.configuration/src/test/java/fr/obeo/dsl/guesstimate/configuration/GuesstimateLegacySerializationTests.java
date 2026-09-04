/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at https://www.eclipse.org/legal/epl-2.0/
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
import fr.obeo.dsl.guesstimate.Sheet;
import fr.obeo.dsl.guesstimate.VariableType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests compatibility with persisted Guesstimate models.
 */
public class GuesstimateLegacySerializationTests {

    @Test
    @DisplayName("Given a legacy formula input cache, when the model is loaded and saved, then the obsolete data is discarded")
    public void givenLegacyFormulaInputCacheWhenModelIsLoadedAndSavedThenObsoleteDataIsDiscarded() throws IOException {
        var resourceFactory = new JSONResourceFactory();
        var resource = resourceFactory.createResourceFromPath("source");
        var sheet = GuesstimateFactory.eINSTANCE.createSheet();
        var formulaVariable = GuesstimateFactory.eINSTANCE.createVariable();
        formulaVariable.setType(VariableType.FORMULA);
        ((FormulaSetting) formulaVariable.getSettings()).setFormula("A");
        sheet.getVariables().add(formulaVariable);
        resource.getContents().add(sheet);

        var output = new ByteArrayOutputStream();
        resource.save(output, Map.of());
        String json = output.toString(StandardCharsets.UTF_8);
        String legacyJson = json.replaceFirst("(\\\"formula\\\"\\s*:\\s*\\\"A\\\")", "$1,\\\"inputs\\\":[\\\"obsolete\\\"]");
        assertThat(legacyJson).isNotEqualTo(json).contains("\"inputs\"");

        var loadedResource = resourceFactory.createResourceFromPath("loaded");
        loadedResource.load(new ByteArrayInputStream(legacyJson.getBytes(StandardCharsets.UTF_8)), Map.of());
        assertThat(loadedResource.getContents()).singleElement().isInstanceOf(Sheet.class);

        var reserializedOutput = new ByteArrayOutputStream();
        loadedResource.save(reserializedOutput, Map.of());
        assertThat(reserializedOutput.toString(StandardCharsets.UTF_8)).doesNotContain("\"inputs\"");
    }
}
