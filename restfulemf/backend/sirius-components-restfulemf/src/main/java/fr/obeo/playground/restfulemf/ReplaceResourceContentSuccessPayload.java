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
package fr.obeo.playground.restfulemf;

import java.util.Objects;
import java.util.UUID;

import org.eclipse.sirius.components.core.api.IPayload;

/**
 * The successful result of a resource replacement.
 *
 * @since 1.1.0
 */
public record ReplaceResourceContentSuccessPayload(UUID id, String revision) implements IPayload {

    public ReplaceResourceContentSuccessPayload {
        Objects.requireNonNull(id);
        Objects.requireNonNull(revision);
    }
}
