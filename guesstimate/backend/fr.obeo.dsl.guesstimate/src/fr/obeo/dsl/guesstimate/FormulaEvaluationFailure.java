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

/**
 * Describes why a formula could not be evaluated.
 *
 * @since 0.0.7
 */
public enum FormulaEvaluationFailure {
    INVALID_SYNTAX,
    UNAVAILABLE_INPUT,
    UNSUPPORTED_EXPRESSION
}
