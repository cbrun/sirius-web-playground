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

import java.util.Optional;

/**
 * Resolves Sirius Web project documents without exposing its domain model.
 *
 * @since 2026.7.3
 */
public interface IProjectDocumentsService {

    Optional<ProjectDocuments> findByProjectId(String projectId);
}
