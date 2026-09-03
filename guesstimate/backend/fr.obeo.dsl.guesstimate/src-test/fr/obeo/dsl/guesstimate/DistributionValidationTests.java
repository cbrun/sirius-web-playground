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

import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.obeo.dsl.guesstimate.simulation.SamplingSimulationAdapter;
import fr.obeo.dsl.guesstimate.util.GuesstimateValidator;

/**
 * Tests the statistical domain constraints and their use by the sampling runtime.
 */
public class DistributionValidationTests {

    private final GuesstimateFactory factory = GuesstimateFactory.eINSTANCE;

    @Test
    @DisplayName("Given newly created settings, when they are validated, then all defaults are valid")
    public void givenNewSettingsWhenValidatedThenDefaultsAreValid() {
        assertThat(List.of(
                this.factory.createNormalDistribution(),
                this.factory.createLogNormalDistribution(),
                this.factory.createUniformDistribution(),
                this.factory.createBetaDistribution(),
                this.factory.createTriangularDistribution(),
                this.factory.createBinomialDistribution(),
                this.factory.createFormulaSetting(),
                this.factory.createPoissonDistribution(),
                this.factory.createExponentialDistribution(),
                this.factory.createGammaDistribution(),
                this.factory.createSheet()))
                .allMatch(this::isValid);
    }

    @Test
    @DisplayName("Given invalid statistical parameters, when they are validated, then each setting is rejected")
    public void givenInvalidParametersWhenValidatedThenEachSettingIsRejected() {
        NormalDistribution normal = this.factory.createNormalDistribution();
        normal.setStandardDeviation(0);
        LogNormalDistribution logNormal = this.factory.createLogNormalDistribution();
        logNormal.setLogStandardDeviation(Double.NaN);
        UniformDistribution uniform = this.factory.createUniformDistribution();
        uniform.setMax(uniform.getMin());
        BetaDistribution beta = this.factory.createBetaDistribution();
        beta.setAlpha(0);
        TriangularDistribution triangular = this.factory.createTriangularDistribution();
        triangular.setMode(2);
        PoissonDistribution poisson = this.factory.createPoissonDistribution();
        poisson.setMean(0);
        ExponentialDistribution exponential = this.factory.createExponentialDistribution();
        exponential.setMean(Double.POSITIVE_INFINITY);
        GammaDistribution gamma = this.factory.createGammaDistribution();
        gamma.setScale(0);
        Sheet sheet = this.factory.createSheet();
        sheet.setSampleSize(0);

        assertThat(List.of(normal, logNormal, uniform, beta, triangular, poisson, exponential, gamma, sheet))
                .noneMatch(this::isValid);
    }

    @Test
    @DisplayName("Given a binomial setting, when its boundaries are validated, then only Commons Math compatible values are accepted")
    public void givenBinomialSettingWhenBoundariesAreValidatedThenOnlyCompatibleValuesAreAccepted() {
        BinomialDistribution binomial = this.factory.createBinomialDistribution();
        binomial.setTrials(0);
        binomial.setProbabilityOfSuccess(0d);
        assertThat(this.isValid(binomial)).isTrue();

        binomial.setProbabilityOfSuccess(1d);
        assertThat(this.isValid(binomial)).isTrue();

        binomial.setTrials(-1);
        assertThat(this.isValid(binomial)).isFalse();

        binomial.setTrials(1);
        binomial.setProbabilityOfSuccess(1.1d);
        assertThat(this.isValid(binomial)).isFalse();

        binomial.setProbabilityOfSuccess(null);
        assertThat(this.isValid(binomial)).isFalse();
    }

    @Test
    @DisplayName("Given a triangular setting, when its mode is on either bound, then the setting is valid")
    public void givenTriangularSettingWhenModeIsOnEitherBoundThenSettingIsValid() {
        TriangularDistribution triangular = this.factory.createTriangularDistribution();
        triangular.setMode(triangular.getMin());
        assertThat(this.isValid(triangular)).isTrue();

        triangular.setMode(triangular.getMax());
        assertThat(this.isValid(triangular)).isTrue();
    }

    @Test
    @DisplayName("Given an existing sample, when distribution parameters become invalid, then the stale sample is cleared")
    public void givenExistingSampleWhenParametersBecomeInvalidThenStaleSampleIsCleared() {
        Sheet sheet = this.factory.createSheet();
        sheet.setSampleSize(4);
		Variable variable = this.factory.createVariable();
		variable.setType(VariableType.BETA);
		BetaDistribution beta = (BetaDistribution) variable.getSettings();
        sheet.getVariables().add(variable);

        SamplingSimulationAdapter adapter = SamplingSimulationAdapter.getOrCreate(variable);
        adapter.resetApacheStateFromSettings();
        assertThat(adapter.getSampleAsDoubles()).hasSize(4);

        beta.setAlpha(0);
        adapter.resetApacheStateFromSettings();
        assertThat(adapter.getSampleAsDoubles()).isNull();
    }

    @Test
    @DisplayName("Given an existing sample, when the sample size becomes invalid and valid again, then sampling safely recovers")
    public void givenExistingSampleWhenSampleSizeChangesThenSamplingSafelyRecovers() {
        Sheet sheet = this.factory.createSheet();
        sheet.setSampleSize(4);
		Variable variable = this.factory.createVariable();
		variable.setType(VariableType.NORMAL);
        sheet.getVariables().add(variable);

        SamplingSimulationAdapter adapter = SamplingSimulationAdapter.getOrCreate(variable);
        adapter.resetApacheStateFromSettings();
        assertThat(adapter.getSampleAsDoubles()).hasSize(4);

        sheet.setSampleSize(0);
        adapter.resample();
        assertThat(adapter.getSampleAsDoubles()).isNull();

        sheet.setSampleSize(4);
        adapter.resample();
        assertThat(adapter.getSampleAsDoubles()).hasSize(4);
    }

    private boolean isValid(EObject object) {
        return GuesstimateValidator.INSTANCE.validate(object, null, new HashMap<>());
    }
}
