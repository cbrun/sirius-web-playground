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
package org.eclipse.sirius.web.restfulemf.application;

import java.io.InputStream;
import java.io.IOException;
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

import org.eclipse.sirius.web.restfulemf.ReplaceResourceContentInput;
import org.eclipse.sirius.web.restfulemf.ReplaceResourceContentSuccessPayload;
import org.eclipse.sirius.web.restfulemf.ResourceRevisionConflictPayload;
import org.eclipse.sirius.web.restfulemf.application.api.IRestfulEMFWriteApplicationService;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceWriteResult;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceWriteStatus;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.services.api.IProjectDocumentsService;
import org.eclipse.sirius.web.restfulemf.services.api.ProjectDocuments;

/**
 * Orchestrates RESTful EMF write use cases.
 */
@Service
public class RestfulEMFWriteApplicationService implements IRestfulEMFWriteApplicationService {

    private static final Duration EVENT_TIMEOUT = Duration.ofSeconds(10);

    private final IProjectDocumentsService projectDocumentsService;

    private final IEditingContextDispatcher editingContextDispatcher;

    private final Logger logger = LoggerFactory.getLogger(RestfulEMFWriteApplicationService.class);

    public RestfulEMFWriteApplicationService(IProjectDocumentsService projectDocumentsService, IEditingContextDispatcher editingContextDispatcher) {
        this.projectDocumentsService = Objects.requireNonNull(projectDocumentsService);
        this.editingContextDispatcher = Objects.requireNonNull(editingContextDispatcher);
    }

    @Override
    public ResourceWriteResult replaceResource(String projectId, String path, ResourceFormat format, InputStream content, List<String> expectedRevisions, boolean createOnly) {
        ProjectDocuments projectDocuments = this.getProjectDocuments(projectId);
        try {
            var input = new ReplaceResourceContentInput(UUID.randomUUID(), path, format, content.readAllBytes(), expectedRevisions, createOnly);
            IPayload payload = this.editingContextDispatcher.dispatchMutation(projectDocuments.editingContextId(), input)
                    .timeout(EVENT_TIMEOUT)
                    .onErrorMap(TimeoutException.class, exception -> new RestfulEMFException(RestfulEMFError.TIMEOUT, "The EMF document replacement timed out", exception))
                    .block();
            if (payload instanceof ReplaceResourceContentSuccessPayload successPayload) {
                return new ResourceWriteResult(successPayload.status(), "");
            } else if (payload instanceof ResourceRevisionConflictPayload conflictPayload) {
                return new ResourceWriteResult(ResourceWriteStatus.CONFLICT, conflictPayload.currentRevision());
            } else if (payload == null || payload instanceof ErrorPayload) {
                throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "The EMF document could not be replaced");
            }
            throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "Unexpected document replacement result");
        } catch (IOException exception) {
            this.logger.atWarn().setMessage("REST EMF request body could not be read")
                    .addKeyValue("projectId", projectId).setCause(exception).log();
            throw new RestfulEMFException(RestfulEMFError.INVALID_RESOURCE, "The request body could not be read", exception);
        } catch (RestfulEMFException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            this.logger.atWarn()
                    .setMessage("Document replacement failed")
                    .addKeyValue("projectId", projectId)
                    .addKeyValue("editingContextId", projectDocuments.editingContextId())
                    .setCause(exception)
                    .log();
            throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "The EMF document could not be replaced", exception);
        }
    }

    private ProjectDocuments getProjectDocuments(String projectId) {
        return this.projectDocumentsService.findByProjectId(projectId)
                .orElseThrow(() -> new RestfulEMFException(RestfulEMFError.NOT_FOUND, "Project not found"));
    }

}
