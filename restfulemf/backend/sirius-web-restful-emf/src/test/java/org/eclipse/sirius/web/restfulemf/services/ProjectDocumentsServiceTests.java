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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.sirius.web.application.project.services.api.IProjectEditingContextService;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.Document;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.SemanticData;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.services.api.ISemanticDataSearchService;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;
import org.junit.jupiter.api.Test;

/**
 * Verifies project-to-semantic-data resolution without crossing repository ownership.
 */
public class ProjectDocumentsServiceTests {

    private static final String PROJECT = "project";

    @Test
    public void givenSemanticDataWhenResolvingThenDocumentMetadataIsPreserved() {
        var projects = mock(IProjectEditingContextService.class);
        var semanticSearch = mock(ISemanticDataSearchService.class);
        UUID editingContextId = UUID.randomUUID();
        var semanticData = mock(SemanticData.class);
        var document = mock(Document.class);
        UUID documentId = UUID.randomUUID();
        when(document.getId()).thenReturn(documentId);
        when(document.getName()).thenReturn("folder/model.ecore");
        when(document.isReadOnly()).thenReturn(true);
        when(semanticData.getDocuments()).thenReturn(Set.of(document));
        when(projects.getEditingContextId(PROJECT)).thenReturn(Optional.of(editingContextId.toString()));
        when(semanticSearch.findById(editingContextId)).thenReturn(Optional.of(semanticData));
        var result = new ProjectDocumentsService(projects, semanticSearch).findByProjectId(PROJECT);
        assertThat(result).isPresent();
        assertThat(result.get().editingContextId()).isEqualTo(editingContextId.toString());
        assertThat(result.get().documents()).containsExactly(new ResourceDocument(documentId, document.getName(), true));
    }

    @Test
    public void givenInvalidOrMissingEditingContextWhenResolvingThenTheResultIsEmpty() {
        var projects = mock(IProjectEditingContextService.class);
        var semanticSearch = mock(ISemanticDataSearchService.class);
        var service = new ProjectDocumentsService(projects, semanticSearch);
        assertThat(service.findByProjectId(PROJECT)).isEmpty();
        when(projects.getEditingContextId(PROJECT)).thenReturn(Optional.of("invalid"));
        assertThat(service.findByProjectId(PROJECT)).isEmpty();
        verifyNoInteractions(semanticSearch);
        when(projects.getEditingContextId(PROJECT)).thenReturn(Optional.of(UUID.randomUUID().toString()));
        assertThat(service.findByProjectId(PROJECT)).isEmpty();
    }
}
