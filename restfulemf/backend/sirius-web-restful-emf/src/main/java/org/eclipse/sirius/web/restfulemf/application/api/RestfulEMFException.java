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
package org.eclipse.sirius.web.restfulemf.application.api;

import java.util.Objects;

/**
 * Signals an application failure without depending on HTTP concepts.
 *
 * @since 2026.7.3
 *
 * @author cbrun
 */
public class RestfulEMFException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final RestfulEMFError error;

    public RestfulEMFException(RestfulEMFError error, String message) {
        super(message);
        this.error = Objects.requireNonNull(error);
    }

    public RestfulEMFException(RestfulEMFError error, String message, Throwable cause) {
        super(message, cause);
        this.error = Objects.requireNonNull(error);
    }

    public RestfulEMFError getError() {
        return this.error;
    }
}
