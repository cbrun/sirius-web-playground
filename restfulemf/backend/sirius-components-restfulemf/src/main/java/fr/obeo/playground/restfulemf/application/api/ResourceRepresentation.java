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
 * A serialized resource and its revision.
 *
 * @since 1.1.0
 */
public record ResourceRepresentation(byte[] content, String revision) {

    public ResourceRepresentation {
        content = Objects.requireNonNull(content).clone();
        Objects.requireNonNull(revision);
    }

    @Override
    public byte[] content() {
        return this.content.clone();
    }
}
