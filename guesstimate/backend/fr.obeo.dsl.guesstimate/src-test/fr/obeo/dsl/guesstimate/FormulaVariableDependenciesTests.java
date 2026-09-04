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
import static org.assertj.core.api.Assertions.within;

import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.obeo.dsl.guesstimate.simulation.SamplingSimulationAdapter;
import fr.obeo.dsl.guesstimate.util.GuesstimateValidator;

/**
 * Tests formula dependency resolution.
 */
public class FormulaVariableDependenciesTests {

    private final GuesstimateFactory factory = GuesstimateFactory.eINSTANCE;

    @Test
    @DisplayName("Given a formula, when it changes, then its referenced variables are computed from its current value")
    public void givenFormulaWhenItChangesThenReferencedVariablesAreComputedFromItsCurrentValue() {
        var sheet = this.factory.createSheet();
        var first = this.factory.createVariable();
        first.setName("A");
        var second = this.factory.createVariable();
        second.setName("B");
        var formula = this.factory.createVariable();
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

    @Test
    @DisplayName("Given formulas declared before their inputs, when the sheet is sampled, then dependencies are evaluated first")
    public void givenFormulasDeclaredBeforeInputsWhenSheetIsSampledThenDependenciesAreEvaluatedFirst() {
        Sheet sheet = this.factory.createSheet();
        sheet.setSampleSize(4);
        Variable first = this.createVariable("A", VariableType.NORMAL);
        Variable second = this.createFormula("B", "A + 1");
        Variable third = this.createFormula("C", "B + 1");
        sheet.getVariables().add(third);
        sheet.getVariables().add(second);
        sheet.getVariables().add(first);

        assertThat(new VariableServices().getVariablesInEvaluationOrder(sheet)).containsExactly(first, second, third);

        sheet.resample();

        double[] firstSample = this.getSample(first);
        double[] secondSample = this.getSample(second);
        double[] thirdSample = this.getSample(third);
        assertThat(firstSample).hasSize(4);
        assertThat(secondSample).hasSize(4);
        assertThat(thirdSample).hasSize(4);
        for (int index = 0; index < firstSample.length; index++) {
            assertThat(secondSample[index]).isCloseTo(firstSample[index] + 1, within(1e-12));
            assertThat(thirdSample[index]).isCloseTo(firstSample[index] + 2, within(1e-12));
        }
    }

    @Test
    @DisplayName("Given a dependency cycle, when formulas are validated and sampled, then the cyclic branch is rejected and isolated")
    public void givenDependencyCycleWhenFormulasAreValidatedAndSampledThenCyclicBranchIsRejectedAndIsolated() {
        Sheet sheet = this.factory.createSheet();
        sheet.setSampleSize(4);
        Variable first = this.createFormula("A", "B + 1");
        Variable second = this.createFormula("B", "C + 1");
        Variable third = this.createFormula("C", "A + 1");
        Variable downstream = this.createFormula("D", "A + 1");
        Variable independent = this.createVariable("E", VariableType.NORMAL);
        sheet.getVariables().addAll(List.of(first, second, third, downstream, independent));
        List.of(first, second, third, downstream).forEach(variable ->
                SamplingSimulationAdapter.getOrCreate(variable).setSample(new double[] { 42 }));

        BasicDiagnostic diagnostic = new BasicDiagnostic();
        assertThat(GuesstimateValidator.INSTANCE.validate(first.getSettings(), diagnostic, new HashMap<>())).isFalse();
        assertThat(diagnostic.getChildren()).anyMatch(child -> child.getMessage().contains("cyclic dependency"));
        assertThat(GuesstimateValidator.INSTANCE.validate(second.getSettings(), null, new HashMap<>())).isFalse();
        assertThat(GuesstimateValidator.INSTANCE.validate(third.getSettings(), null, new HashMap<>())).isFalse();
        assertThat(GuesstimateValidator.INSTANCE.validate(downstream.getSettings(), null, new HashMap<>())).isFalse();

        sheet.resample();

        assertThat(this.getSample(first)).isNull();
        assertThat(this.getSample(second)).isNull();
        assertThat(this.getSample(third)).isNull();
        assertThat(this.getSample(downstream)).isNull();
        assertThat(this.getSample(independent)).hasSize(4);
    }

    @Test
    @DisplayName("Given an unavailable formula input, when a dependent formula is sampled, then no partial sample is produced")
    public void givenUnavailableFormulaInputWhenDependentFormulaIsSampledThenNoPartialSampleIsProduced() {
        Sheet sheet = this.factory.createSheet();
        sheet.setSampleSize(4);
        Variable dependent = this.createFormula("B", "A + 1");
        Variable unavailable = this.createFormula("A", "unknown + 1");
        sheet.getVariables().add(dependent);
        sheet.getVariables().add(unavailable);

        sheet.resample();

        assertThat(this.getSample(unavailable)).isNull();
        assertThat(this.getSample(dependent)).isNull();
    }

    private Variable createVariable(String name, VariableType type) {
        Variable variable = this.factory.createVariable();
        variable.setName(name);
        variable.setType(type);
        return variable;
    }

    private Variable createFormula(String name, String formula) {
        Variable variable = this.createVariable(name, VariableType.FORMULA);
        ((FormulaSetting) variable.getSettings()).setFormula(formula);
        return variable;
    }

    private double[] getSample(Variable variable) {
        return SamplingSimulationAdapter.getOrCreate(variable).getSampleAsDoubles();
    }
}
