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
package fr.obeo.dsl.guesstimate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests formula dependency resolution.
 */
public class FormulaVariableDependenciesTests {

    @Test
    @DisplayName("Given a formula, when it changes, then its referenced variables are computed from its current value")
    public void givenFormulaWhenItChangesThenReferencedVariablesAreComputedFromItsCurrentValue() {
        var factory = GuesstimateFactory.eINSTANCE;
        var sheet = factory.createSheet();
        var first = factory.createVariable();
        first.setName("A");
        var second = factory.createVariable();
        second.setName("B");
        var formula = factory.createVariable();
        formula.setType(VariableType.FORMULA);
        sheet.getVariables().add(first);
        sheet.getVariables().add(second);
        sheet.getVariables().add(formula);

        var formulaSetting = (FormulaSetting) formula.getSettings();
        formulaSetting.setFormula("B + A + B + unknown");

        var variableServices = new VariableServices();
        assertThat(GuesstimatePackage.eINSTANCE.getFormulaSetting().getEStructuralFeature("inputs")).isNull();
        assertThat(variableServices.getReferencedVariables(formula)).containsExactly(second, first);

        formulaSetting.setFormula("A");
        assertThat(variableServices.getReferencedVariables(formula)).containsExactly(first);

        formulaSetting.setFormula("(");
        assertThat(variableServices.getReferencedVariables(formula)).isEmpty();
        assertThat(variableServices.getReferencedVariables(first)).isEmpty();

        sheet.getVariables().remove(formula);
        formulaSetting.setFormula("A");
        assertThat(variableServices.getReferencedVariables(formula)).isEmpty();
    }
}
