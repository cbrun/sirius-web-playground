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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.petitparser.context.Result;

import com.google.common.primitives.Doubles;

import fr.obeo.dsl.guesstimate.formula.ArithParser;
import fr.obeo.dsl.guesstimate.simulation.SamplingSimulationAdapter;

/**
 * Evaluates formula variables against the samples of their dependencies.
 *
 * @since 0.0.7
 */
public class FormulaVariableComputer {

    /**
     * Evaluates a formula without changing the model or its simulation adapter.
     *
     * @param sheet the containing sheet
     * @param variables the variables available by name
     * @param variable the formula variable to evaluate
     * @return the computed sample or a failure category
     * @since 0.0.7
     */
    public FormulaEvaluationResult compute(Sheet sheet, Map<String, Variable> variables, Variable variable) {
        if (!(variable.getSettings() instanceof FormulaSetting formulaSetting) || formulaSetting.getFormula() == null) {
            return FormulaEvaluationResult.failure(FormulaEvaluationFailure.INVALID_SYNTAX);
        }
        Result parseResult = new ArithParser().parse(formulaSetting.getFormula());
        if (parseResult.isFailure()) {
            return FormulaEvaluationResult.failure(FormulaEvaluationFailure.INVALID_SYNTAX);
        }
        return this.evaluate(parseResult.get(), sheet.getSampleSize(), variables, variable);
    }

    private FormulaEvaluationResult evaluate(Object expression, int sampleSize, Map<String, Variable> variables, Variable variable) {
        if (expression instanceof Number number) {
            return this.constant(number.doubleValue(), sampleSize);
        } else if (expression instanceof String value) {
            Double number = Doubles.tryParse(value);
            return number != null ? this.constant(number, sampleSize) : this.variable(value, sampleSize, variables, variable);
        } else if (expression instanceof List<?> parts) {
            if (parts.size() == 3 && Character.valueOf('(').equals(parts.get(0)) && Character.valueOf(')').equals(parts.get(2))) {
                return this.evaluate(parts.get(1), sampleSize, variables, variable);
            } else if (parts.size() == 2 && Character.valueOf('-').equals(parts.get(0))) {
                return this.evaluateUnaryMinus(parts.get(1), sampleSize, variables, variable);
            } else if (parts.size() == 3 && parts.get(1) instanceof Character operator) {
                return this.evaluateBinary(parts.get(0), operator, parts.get(2), sampleSize, variables, variable);
            }
        }
        return FormulaEvaluationResult.failure(FormulaEvaluationFailure.UNSUPPORTED_EXPRESSION);
    }

    private FormulaEvaluationResult constant(double value, int sampleSize) {
        double[] sample = new double[sampleSize];
        Arrays.fill(sample, value);
        return FormulaEvaluationResult.success(sample);
    }

    private FormulaEvaluationResult variable(String name, int sampleSize, Map<String, Variable> variables, Variable variable) {
        Variable referencedVariable = variables.get(name);
        if (referencedVariable != null && referencedVariable != variable) {
            double[] sample = SamplingSimulationAdapter.find(referencedVariable)
                    .map(SamplingSimulationAdapter::getSampleAsDoubles)
                    .orElse(null);
            if (sample != null && sample.length == sampleSize) {
                return FormulaEvaluationResult.success(sample);
            }
        }
        return FormulaEvaluationResult.failure(FormulaEvaluationFailure.UNAVAILABLE_INPUT);
    }

    private FormulaEvaluationResult evaluateUnaryMinus(Object operand, int sampleSize, Map<String, Variable> variables, Variable variable) {
        FormulaEvaluationResult result = this.evaluate(operand, sampleSize, variables, variable);
        if (result.failure().isPresent()) {
            return result;
        }
        double[] sample = result.sample().orElseThrow();
        for (int index = 0; index < sample.length; index++) {
            sample[index] = -sample[index];
        }
        return FormulaEvaluationResult.success(sample);
    }

    private FormulaEvaluationResult evaluateBinary(Object leftExpression, char operator, Object rightExpression, int sampleSize,
            Map<String, Variable> variables, Variable variable) {
        FormulaEvaluationResult leftResult = this.evaluate(leftExpression, sampleSize, variables, variable);
        if (leftResult.failure().isPresent()) {
            return leftResult;
        }
        FormulaEvaluationResult rightResult = this.evaluate(rightExpression, sampleSize, variables, variable);
        if (rightResult.failure().isPresent()) {
            return rightResult;
        }
        if (operator != '+' && operator != '-' && operator != '*' && operator != '/' && operator != '^') {
            return FormulaEvaluationResult.failure(FormulaEvaluationFailure.UNSUPPORTED_EXPRESSION);
        }
        double[] left = leftResult.sample().orElseThrow();
        double[] right = rightResult.sample().orElseThrow();
        double[] sample = new double[sampleSize];
        for (int index = 0; index < sampleSize; index++) {
            sample[index] = switch (operator) {
                case '+' -> left[index] + right[index];
                case '-' -> left[index] - right[index];
                case '*' -> left[index] * right[index];
                case '/' -> left[index] / right[index];
                case '^' -> Math.pow(left[index], right[index]);
                default -> throw new IllegalStateException("Unsupported operator");
            };
        }
        return FormulaEvaluationResult.success(sample);
    }
}
