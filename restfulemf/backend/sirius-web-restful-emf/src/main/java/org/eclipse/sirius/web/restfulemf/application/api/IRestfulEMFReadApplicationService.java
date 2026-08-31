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
package org.eclipse.sirius.web.restfulemf.application.api;

import java.util.Map;

/**
 * Read use cases exposed by the RESTful EMF application layer.
 *
 * @since 2026.7.3
 */
public interface IRestfulEMFReadApplicationService {

    Map<String, String> getDocuments(String projectId);

    IResourceWriter getEPackages(String projectId);

    ResourceRepresentation getResource(String projectId, String documentSelector, ResourceFormat format, String separator);
}
