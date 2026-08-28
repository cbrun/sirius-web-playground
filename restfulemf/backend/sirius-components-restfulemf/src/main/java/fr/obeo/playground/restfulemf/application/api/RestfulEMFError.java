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
package fr.obeo.playground.restfulemf.application.api;

/**
 * Application errors exposed to the REST adapter.
 *
 * @since 1.1.0
 */
public enum RestfulEMFError {
    CAPABILITY_DENIED,
    NOT_FOUND,
    READ_ONLY,
    INVALID_RESOURCE,
    TIMEOUT,
    PROCESSING_FAILURE
}
