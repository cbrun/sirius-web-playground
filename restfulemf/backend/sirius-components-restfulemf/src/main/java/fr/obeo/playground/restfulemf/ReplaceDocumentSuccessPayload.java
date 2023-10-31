package fr.obeo.playground.restfulemf;
/*******************************************************************************
 * Copyright (c) 2019, 2022 Obeo.
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


import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.web.services.api.document.Document;

/**
 * @author Cedric Brun <cedric.brun@obeo.fr>
 */
public final class ReplaceDocumentSuccessPayload implements IPayload {

    private final UUID id;

    private final Document document;

    public ReplaceDocumentSuccessPayload(UUID id, Document document) {
        this.id = Objects.requireNonNull(id);
        this.document = Objects.requireNonNull(document);
    }

    @Override
    public UUID id() {
        return this.id;
    }

    public Document getDocument() {
        return this.document;
    }

    @Override
    public String toString() {
        String pattern = "{0} '{'id: {1}, document: '{'id: {2}, name: {3}'}''}'";
        return MessageFormat.format(pattern, this.getClass().getSimpleName(), this.id, this.document.getId(), this.getDocument().getName());
    }
}
