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
import java.util.List;
import java.util.UUID;

import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;

/**
 * The successful result of a resource snapshot query.
 *
 * @since 2026.7.3
 */
public record GetResourceContentSuccessPayload(UUID id, ResourceSnapshot snapshot, ResourceDocument document, List<ResourceDocument> documents) implements IPayload {

    public GetResourceContentSuccessPayload {
        Objects.requireNonNull(id);
        Objects.requireNonNull(snapshot);
        Objects.requireNonNull(document);
        documents = List.copyOf(documents);
    }
}
