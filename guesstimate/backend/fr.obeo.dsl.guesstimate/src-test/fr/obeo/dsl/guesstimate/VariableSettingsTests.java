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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests the single source of truth used for a variable's kind and settings.
 */
public class VariableSettingsTests {

    private final GuesstimateFactory factory = GuesstimateFactory.eINSTANCE;

    @Test
    @DisplayName("The metamodel exposes a derived type backed by mandatory settings")
    public void metamodelContract() {
        EAttribute type = GuesstimatePackage.eINSTANCE.getVariable_Type();
        EReference settings = GuesstimatePackage.eINSTANCE.getVariable_Settings();

        assertThat(type.isDerived()).isTrue();
        assertThat(type.isTransient()).isTrue();
        assertThat(type.isVolatile()).isTrue();
        assertThat(type.isChangeable()).isTrue();
        assertThat(type.getLowerBound()).isOne();
        assertThat(settings.isContainment()).isTrue();
        assertThat(settings.getLowerBound()).isOne();
        assertThat(settings.getUpperBound()).isOne();
        assertThat(GuesstimatePackage.eINSTANCE.getVariableType().getEEnumLiteral("computed")).isNull();
        assertThat(GuesstimatePackage.eINSTANCE.getFormulaSetting_Formula().getDefaultValueLiteral()).isEqualTo("0");
    }

    @Test
    @DisplayName("Changing the type creates matching settings with their defaults")
    public void typeCreatesSettings() {
        Variable variable = this.factory.createVariable();

        for (VariableType type : VariableType.values()) {
            variable.setType(type);
            assertThat(variable.getType()).isEqualTo(type);
            assertThat(variable.getSettings()).isInstanceOf(expectedSettingsClass(type));
        }
        variable.setType(VariableType.FORMULA);
        assertThat(((FormulaSetting) variable.getSettings()).getFormula()).isEqualTo("0");
    }

    @Test
    @DisplayName("Changing type replaces settings while selecting the same type preserves them")
    public void typeChangeReplacementPolicy() {
        Variable variable = this.factory.createVariable();
        variable.setType(VariableType.NORMAL);
        VariableSettings normal = variable.getSettings();
        ((NormalDistribution) normal).setMean(42);

        variable.setType(VariableType.NORMAL);
        assertThat(variable.getSettings()).isSameAs(normal);
        assertThat(((NormalDistribution) variable.getSettings()).getMean()).isEqualTo(42);

        variable.setType(VariableType.BETA);
        assertThat(variable.getSettings()).isNotSameAs(normal).isInstanceOf(BetaDistribution.class);
        assertThat(normal.eContainer()).isNull();
    }

    @Test
    @DisplayName("Replacing settings updates the derived type and notifies both public features")
    public void directSettingsChangeUpdatesTypeAndNotifications() {
        Variable variable = this.factory.createVariable();
        variable.setType(VariableType.NORMAL);
        List<Object> features = new ArrayList<>();
        variable.eAdapters().add(new AdapterImpl() {
            @Override
            public void notifyChanged(Notification notification) {
                features.add(notification.getFeature());
            }
        });

        variable.setSettings(this.factory.createGammaDistribution());

        assertThat(variable.getType()).isEqualTo(VariableType.GAMMA);
        assertThat(features).containsExactly(
                GuesstimatePackage.eINSTANCE.getVariable_Settings(),
                GuesstimatePackage.eINSTANCE.getVariable_Type());
    }

    @Test
    @DisplayName("A bare variable remains explicitly incomplete until a type is selected")
    public void bareVariableIsIncomplete() {
        Variable variable = this.factory.createVariable();

        assertThat(variable.getSettings()).isNull();
        assertThat(variable.getType()).isNull();
    }

    private Class<? extends VariableSettings> expectedSettingsClass(VariableType type) {
        return switch (type) {
            case FORMULA -> FormulaSetting.class;
            case NORMAL -> NormalDistribution.class;
            case UNIFORM -> UniformDistribution.class;
            case LOGNORMAL -> LogNormalDistribution.class;
            case BETA -> BetaDistribution.class;
            case TRIANGULAR -> TriangularDistribution.class;
            case BINOMIAL -> BinomialDistribution.class;
            case POISSON -> PoissonDistribution.class;
            case EXPONENTIAL -> ExponentialDistribution.class;
            case GAMMA -> GammaDistribution.class;
        };
    }
}
