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
package org.eclipse.sirius.web.restfulemf.services.api;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.sirius.web.restfulemf.ResourceSnapshot;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;

/**
 * Converts canonical Sirius Web resource snapshots to and from external EMF formats.
 *
 * @since 2026.7.3
 */
public interface IResourceFormatService {

    void serializeEPackages(String projectId, OutputStream outputStream);

    void serialize(ResourceSnapshot snapshot, ResourceDocument document, ResourceFormat format, String separator, OutputStream outputStream);

    ResourceSnapshot deserialize(InputStream content, ResourceDocument document, ResourceFormat format);
}
