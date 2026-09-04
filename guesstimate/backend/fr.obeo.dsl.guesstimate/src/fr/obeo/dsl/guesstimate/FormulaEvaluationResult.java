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

import java.util.Objects;
import java.util.Optional;

/**
 * Immutable result of a formula evaluation.
 *
 * @param sample the computed sample on success
 * @param failure the failure category otherwise
 * @since 0.0.7
 */
public record FormulaEvaluationResult(Optional<double[]> sample, Optional<FormulaEvaluationFailure> failure) {

    public FormulaEvaluationResult {
        Objects.requireNonNull(sample);
        Objects.requireNonNull(failure);
        if (sample.isPresent() == failure.isPresent()) {
            throw new IllegalArgumentException("A formula evaluation must contain either a sample or a failure");
        }
        sample = sample.map(double[]::clone);
    }

    @Override
    public Optional<double[]> sample() {
        return this.sample.map(double[]::clone);
    }

    /**
     * Creates a successful result.
     *
     * @param sample the computed sample
     * @return a successful result
     * @since 0.0.7
     */
    public static FormulaEvaluationResult success(double[] sample) {
        return new FormulaEvaluationResult(Optional.of(sample), Optional.empty());
    }

    /**
     * Creates a failed result.
     *
     * @param failure the failure category
     * @return a failed result
     * @since 0.0.7
     */
    public static FormulaEvaluationResult failure(FormulaEvaluationFailure failure) {
        return new FormulaEvaluationResult(Optional.empty(), Optional.of(failure));
    }
}
