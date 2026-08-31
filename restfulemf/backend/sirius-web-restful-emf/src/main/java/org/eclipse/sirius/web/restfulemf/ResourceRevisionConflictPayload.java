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
package org.eclipse.sirius.web.restfulemf;

import java.util.Objects;
import java.util.UUID;

import org.eclipse.sirius.components.core.api.IPayload;

/**
 * Indicates that a resource replacement was based on an outdated revision.
 *
 * @since 2026.7.3
 */
public record ResourceRevisionConflictPayload(UUID id, String currentRevision) implements IPayload {

    public ResourceRevisionConflictPayload {
        Objects.requireNonNull(id);
        Objects.requireNonNull(currentRevision);
    }
}
