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
package fr.obeo.playground.restfulemf.services.api;

import fr.obeo.playground.restfulemf.ResourceSnapshot;
import fr.obeo.playground.restfulemf.application.api.ResourceFormat;

/**
 * Converts canonical Sirius Web resource snapshots to and from external EMF formats.
 *
 * @since 1.1.0
 */
public interface IResourceFormatService {

    byte[] serializeEPackages(String projectId);

    byte[] serialize(ResourceSnapshot snapshot, ResourceDocument document, ResourceFormat format, String separator);

    ResourceSnapshot deserialize(byte[] content, ResourceDocument document, ResourceFormat format);
}
