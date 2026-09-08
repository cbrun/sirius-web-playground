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
package org.eclipse.sirius.web.restfulemf.services;

import java.util.Objects;
import java.util.UUID;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.emf.services.EObjectIDManager;

/**
 * Gives omitted JSON object identifiers reproducible values in a detached snapshot only.
 * The JSON serializer omits identifiers for some Ecore objects, including operations and annotations.
 *
 * @author cbrun
 */
public class SnapshotEObjectIDManager extends EObjectIDManager {

    private final UUID documentId;

    public SnapshotEObjectIDManager(UUID documentId) {
        this.documentId = Objects.requireNonNull(documentId);
    }

    @Override
    public String getOrCreateId(EObject object) {
        return this.findId(object).orElseGet(() -> new ResourceReferences().objectId(this.documentId, object.eResource().getURIFragment(object)));
    }
}
