/*******************************************************************************
 * Copyright (c) 2019, 2026 Obeo.
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

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.sirius.components.core.api.IInput;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;

/**
 * Describes the replacement of an EMF resource content.
 */
public record ReplaceResourceContentInput(UUID id, String path, ResourceFormat format, byte[] content, List<String> expectedRevisions, boolean createOnly) implements IInput {

    public ReplaceResourceContentInput {
        Objects.requireNonNull(id);
        Objects.requireNonNull(path);
        Objects.requireNonNull(format);
        content = Objects.requireNonNull(content).clone();
        expectedRevisions = List.copyOf(expectedRevisions);
    }

    @Override
    public byte[] content() {
        return this.content.clone();
    }
}
