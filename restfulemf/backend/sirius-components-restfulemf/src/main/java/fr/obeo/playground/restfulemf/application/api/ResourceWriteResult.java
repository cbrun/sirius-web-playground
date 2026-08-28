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

import java.util.Objects;

/**
 * The outcome of a resource write and the current revision.
 *
 * @since 1.1.0
 */
public record ResourceWriteResult(ResourceWriteStatus status, String revision) {

    public ResourceWriteResult {
        Objects.requireNonNull(status);
        Objects.requireNonNull(revision);
    }
}
