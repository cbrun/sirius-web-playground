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
import java.util.Optional;
import java.util.UUID;

import org.eclipse.sirius.web.application.project.services.api.IProjectEditingContextService;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.services.api.ISemanticDataSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.eclipse.sirius.web.restfulemf.services.api.IProjectDocumentsService;
import org.eclipse.sirius.web.restfulemf.services.api.ProjectDocuments;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;

/**
 * Resolves the Sirius Web domain objects required by RESTful EMF.
 */
@Service
public class ProjectDocumentsService implements IProjectDocumentsService {

    private final IProjectEditingContextService projectEditingContextService;

    private final ISemanticDataSearchService semanticDataSearchService;

    private final Logger logger = LoggerFactory.getLogger(ProjectDocumentsService.class);

    public ProjectDocumentsService(IProjectEditingContextService projectEditingContextService, ISemanticDataSearchService semanticDataSearchService) {
        this.projectEditingContextService = Objects.requireNonNull(projectEditingContextService);
        this.semanticDataSearchService = Objects.requireNonNull(semanticDataSearchService);
    }

    @Override
    public Optional<ProjectDocuments> findByProjectId(String projectId) {
        return this.projectEditingContextService.getEditingContextId(projectId).flatMap(editingContextId -> {
            try {
                return this.semanticDataSearchService.findById(UUID.fromString(editingContextId))
                        .map(semanticData -> new ProjectDocuments(editingContextId, semanticData.getDocuments().stream()
                                .map(document -> new ResourceDocument(document.getId(), document.getName(), document.isReadOnly()))
                                .toList()));
            } catch (IllegalArgumentException exception) {
                this.logger.atWarn()
                        .setMessage("Invalid editing context identifier")
                        .addKeyValue("projectId", projectId)
                        .addKeyValue("editingContextId", editingContextId)
                        .setCause(exception)
                        .log();
                return Optional.empty();
            }
        });
    }
}
