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

import java.util.LinkedHashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

/**
 * Keeps the binary extrinsic-ID table in insertion order, making representation digests reproducible.
 *
 * @author cbrun
 */
public class OrderedXMIResource extends XMIResourceImpl {

    public OrderedXMIResource(URI uri) {
        super(uri);
        this.eObjectToIDMap = new LinkedHashMap<>();
    }
}
