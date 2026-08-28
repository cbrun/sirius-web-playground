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
package fr.obeo.playground.restfulemf.application;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.eclipse.sirius.components.core.api.ErrorPayload;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.graphql.api.IEditingContextDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.obeo.playground.restfulemf.ReplaceResourceContentInput;
import fr.obeo.playground.restfulemf.ReplaceResourceContentSuccessPayload;
import fr.obeo.playground.restfulemf.ResourceRevisionConflictPayload;
import fr.obeo.playground.restfulemf.ResourceSnapshot;
import fr.obeo.playground.restfulemf.application.api.IRestfulEMFWriteApplicationService;
import fr.obeo.playground.restfulemf.application.api.ResourceFormat;
import fr.obeo.playground.restfulemf.application.api.ResourceWriteResult;
import fr.obeo.playground.restfulemf.application.api.ResourceWriteStatus;
import fr.obeo.playground.restfulemf.application.api.RestfulEMFError;
import fr.obeo.playground.restfulemf.application.api.RestfulEMFException;
import fr.obeo.playground.restfulemf.services.api.IProjectDocumentsService;
import fr.obeo.playground.restfulemf.services.api.IResourceFormatService;
import fr.obeo.playground.restfulemf.services.api.ProjectDocuments;
import fr.obeo.playground.restfulemf.services.api.ResourceDocument;

/**
 * Orchestrates RESTful EMF write use cases.
 */
@Service
public class RestfulEMFWriteApplicationService implements IRestfulEMFWriteApplicationService {

    private static final Duration EVENT_TIMEOUT = Duration.ofSeconds(10);

    private final IProjectDocumentsService projectDocumentsService;

    private final IEditingContextDispatcher editingContextDispatcher;

    private final IResourceFormatService resourceFormatService;

    private final Logger logger = LoggerFactory.getLogger(RestfulEMFWriteApplicationService.class);

    public RestfulEMFWriteApplicationService(IProjectDocumentsService projectDocumentsService, IEditingContextDispatcher editingContextDispatcher,
            IResourceFormatService resourceFormatService) {
        this.projectDocumentsService = Objects.requireNonNull(projectDocumentsService);
        this.editingContextDispatcher = Objects.requireNonNull(editingContextDispatcher);
        this.resourceFormatService = Objects.requireNonNull(resourceFormatService);
    }

    @Override
    public ResourceWriteResult replaceResource(String projectId, String documentSelector, ResourceFormat format, byte[] content, List<String> expectedRevisions) {
        ProjectDocuments projectDocuments = this.getProjectDocuments(projectId);
        ResourceDocument document = this.findDocument(projectDocuments, documentSelector);
        if (document.readOnly()) {
            throw new RestfulEMFException(RestfulEMFError.READ_ONLY, "The document is read-only");
        }

        ResourceSnapshot newSnapshot = this.resourceFormatService.deserialize(content, document, format);
        var input = new ReplaceResourceContentInput(UUID.randomUUID(), document.id().toString(), newSnapshot.content(), expectedRevisions);
        try {
            IPayload payload = this.editingContextDispatcher.dispatchMutation(projectDocuments.editingContextId(), input)
                    .timeout(EVENT_TIMEOUT)
                    .onErrorMap(TimeoutException.class, exception -> new RestfulEMFException(RestfulEMFError.TIMEOUT, "The EMF document replacement timed out", exception))
                    .block();
            if (payload instanceof ReplaceResourceContentSuccessPayload successPayload) {
                return new ResourceWriteResult(ResourceWriteStatus.SUCCESS, successPayload.revision());
            } else if (payload instanceof ResourceRevisionConflictPayload conflictPayload) {
                return new ResourceWriteResult(ResourceWriteStatus.CONFLICT, conflictPayload.currentRevision());
            } else if (payload == null || payload instanceof ErrorPayload) {
                throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "The EMF document could not be replaced");
            }
            throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "Unexpected document replacement result");
        } catch (RestfulEMFException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            this.logger.atWarn()
                    .setMessage("Document replacement failed")
                    .addKeyValue("projectId", projectId)
                    .addKeyValue("editingContextId", projectDocuments.editingContextId())
                    .addKeyValue("documentId", document.id())
                    .setCause(exception)
                    .log();
            throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "The EMF document could not be replaced", exception);
        }
    }

    private ProjectDocuments getProjectDocuments(String projectId) {
        return this.projectDocumentsService.findByProjectId(projectId)
                .orElseThrow(() -> new RestfulEMFException(RestfulEMFError.NOT_FOUND, "Project not found"));
    }

    private ResourceDocument findDocument(ProjectDocuments projectDocuments, String documentSelector) {
        return projectDocuments.documents().stream()
                .filter(document -> document.name().equals(documentSelector) || document.id().toString().equals(documentSelector))
                .findFirst()
                .orElseThrow(() -> new RestfulEMFException(RestfulEMFError.NOT_FOUND, "Document not found"));
    }
}
