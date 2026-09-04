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
package fr.obeo.dsl.guesstimate;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.obeo.dsl.guesstimate.formula.ArithParser;
import fr.obeo.dsl.guesstimate.util.GuesstimateValidator;

/**
 * Tests variable identifier validation and resolution.
 */
public class VariableIdentityTests {

    private final GuesstimateFactory factory = GuesstimateFactory.eINSTANCE;

    @Test
    @DisplayName("Given a variable name, when it is checked, then it follows the formula parser identifier syntax")
    public void givenVariableNameWhenItIsCheckedThenItFollowsTheFormulaParserIdentifierSyntax() {
        ArithParser parser = new ArithParser();

        assertThat(parser.isValidIdentifier("A")).isTrue();
        assertThat(parser.isValidIdentifier("A1")).isTrue();
        assertThat(parser.isValidIdentifier("1A")).isFalse();
        assertThat(parser.isValidIdentifier("A_B")).isFalse();
        assertThat(parser.isValidIdentifier("A+B")).isFalse();
        assertThat(parser.isValidIdentifier("A B")).isFalse();
        assertThat(parser.isValidIdentifier("")).isFalse();
    }

    @Test
    @DisplayName("Given an invalid variable name, when the variable is validated, then the name feature is diagnosed")
    public void givenInvalidVariableNameWhenTheVariableIsValidatedThenTheNameFeatureIsDiagnosed() {
        Variable variable = this.factory.createVariable();
        variable.setName("A_B");

        Diagnostic diagnostic = Diagnostician.INSTANCE.validate(variable);

        assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
        assertThat(diagnostic.getChildren()).anySatisfy(child -> {
            assertThat(child.getMessage()).contains("invalid identifier");
            this.assertNameDiagnostic(child, variable);
        });
    }

    @Test
    @DisplayName("Given a variable edited through the VSM service, when its name is entered, then the parser rule is reused")
    public void givenVariableEditedThroughTheVsmServiceWhenItsNameIsEnteredThenTheParserRuleIsReused() {
        Variable variable = this.createVariable("A");
        Services services = new Services();

        services.smartEdit(variable, "A_B");
        assertThat(variable.getName()).isEqualTo("A");
        assertThat(variable.getDocumentation()).isEqualTo("A_B");

        services.smartEdit(variable, "A1");
        assertThat(variable.getName()).isEqualTo("A1");
    }

    @Test
    @DisplayName("Given duplicate variable names, when a sheet is validated, then every duplicate is diagnosed")
    public void givenDuplicateVariableNamesWhenASheetIsValidatedThenEveryDuplicateIsDiagnosed() {
        Sheet sheet = this.factory.createSheet();
        Variable first = this.createVariable("A");
        Variable second = this.createVariable("A");
        sheet.getVariables().add(first);
        sheet.getVariables().add(second);

        Diagnostic diagnostic = Diagnostician.INSTANCE.validate(sheet);

        assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.ERROR);
        assertThat(diagnostic.getChildren()).anySatisfy(child -> {
            assertThat(child.getMessage()).contains("used by another variable");
            this.assertNameDiagnostic(child, first);
        });
        assertThat(diagnostic.getChildren()).anySatisfy(child -> {
            assertThat(child.getMessage()).contains("used by another variable");
            this.assertNameDiagnostic(child, second);
        });
    }

    @Test
    @DisplayName("Given duplicate variable names, when accessible variables are collected, then the ambiguous name is excluded")
    public void givenDuplicateVariableNamesWhenAccessibleVariablesAreCollectedThenTheAmbiguousNameIsExcluded() {
        Sheet sheet = this.factory.createSheet();
        sheet.getVariables().add(this.createVariable("A"));
        sheet.getVariables().add(this.createVariable("A"));
        sheet.getVariables().add(this.createVariable("B"));

        assertThat(new VariableServices().collectAccessibleVariables(sheet))
                .containsOnlyKeys("B");
    }

    private Variable createVariable(String name) {
        Variable variable = this.factory.createVariable();
        variable.setName(name);
        variable.setType(VariableType.NORMAL);
        return variable;
    }

    private void assertNameDiagnostic(Diagnostic diagnostic, Variable variable) {
        assertThat(diagnostic.getData()).hasSize(2);
        assertThat(diagnostic.getData().get(0)).isSameAs(variable);
        assertThat(diagnostic.getData().get(1)).isSameAs(GuesstimatePackage.eINSTANCE.getVariable_Name());
    }
}
