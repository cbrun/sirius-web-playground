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
package fr.obeo.playground.restfulemf;

import java.util.Objects;
import java.util.UUID;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.components.core.api.IInput;

/**
 * Describes the replacement of an EMF resource content.
 */
public record ReplaceResourceContentInput(UUID id, Resource newResourceContent) implements IInput {

    public ReplaceResourceContentInput {
        Objects.requireNonNull(id);
        Objects.requireNonNull(newResourceContent);
    }
}
