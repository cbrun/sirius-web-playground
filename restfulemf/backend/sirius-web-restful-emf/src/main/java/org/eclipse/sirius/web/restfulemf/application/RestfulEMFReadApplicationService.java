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

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.graphql.api.IEditingContextDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.eclipse.sirius.web.restfulemf.GetResourceContentInput;
import org.eclipse.sirius.web.restfulemf.GetResourceContentSuccessPayload;
import org.eclipse.sirius.web.restfulemf.application.api.IRestfulEMFReadApplicationService;
import org.eclipse.sirius.web.restfulemf.application.api.IResourceWriter;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceRepresentation;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.services.api.IProjectDocumentsService;
import org.eclipse.sirius.web.restfulemf.services.api.IResourceFormatService;
import org.eclipse.sirius.web.restfulemf.services.api.ProjectDocuments;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;
import org.eclipse.sirius.web.restfulemf.services.ResourcePaths;

/**
 * Orchestrates RESTful EMF read use cases.
 */
@Service
public class RestfulEMFReadApplicationService implements IRestfulEMFReadApplicationService {

    private static final Duration EVENT_TIMEOUT = Duration.ofSeconds(10);

    private final IProjectDocumentsService projectDocumentsService;

    private final IEditingContextDispatcher editingContextDispatcher;

    private final IResourceFormatService resourceFormatService;

    private final ResourcePaths resourcePaths;

    private final Logger logger = LoggerFactory.getLogger(RestfulEMFReadApplicationService.class);

    public RestfulEMFReadApplicationService(IProjectDocumentsService projectDocumentsService, IEditingContextDispatcher editingContextDispatcher,
            IResourceFormatService resourceFormatService, ResourcePaths resourcePaths) {
        this.projectDocumentsService = Objects.requireNonNull(projectDocumentsService);
        this.editingContextDispatcher = Objects.requireNonNull(editingContextDispatcher);
        this.resourceFormatService = Objects.requireNonNull(resourceFormatService);
        this.resourcePaths = Objects.requireNonNull(resourcePaths);
    }

    @Override
    public List<ResourceDocument> getDocuments(String projectId) {
        return this.resourcePaths.assignPaths(this.getProjectDocuments(projectId).documents());
    }

    @Override
    public ResourceRepresentation getEPackages(String projectId, ResourceFormat format) {
        this.getProjectDocuments(projectId);
        return new ResourceRepresentation(outputStream -> this.resourceFormatService.serializeEPackages(projectId, format, outputStream),
                this.resourceFormatService.ePackagesRevision(projectId, format));
    }

    @Override
    public ResourceRepresentation getResource(String projectId, String documentSelector, ResourceFormat format, String separator) {
        ProjectDocuments projectDocuments = this.getProjectDocuments(projectId);
        this.resourcePaths.validate(documentSelector);
        var input = new GetResourceContentInput(UUID.randomUUID(), documentSelector);
        try {
            IPayload payload = this.editingContextDispatcher.dispatchQuery(projectDocuments.editingContextId(), input)
                    .timeout(EVENT_TIMEOUT)
                    .onErrorMap(TimeoutException.class, exception -> new RestfulEMFException(RestfulEMFError.TIMEOUT, "The EMF document read timed out", exception))
                    .block();
            if (payload instanceof GetResourceContentSuccessPayload successPayload) {
                IResourceWriter writer = outputStream -> this.resourceFormatService.serialize(successPayload.snapshot(), successPayload.document(), successPayload.documents(),
                        format, separator, outputStream);
                return new ResourceRepresentation(writer, this.resourceFormatService.representationRevision(successPayload.snapshot(), successPayload.document(),
                        successPayload.documents(), format, separator));
            }
            throw new RestfulEMFException(RestfulEMFError.NOT_FOUND, "Document resource not found");
        } catch (RestfulEMFException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            this.logger.atWarn()
                    .setMessage("Document resource query failed")
                    .addKeyValue("projectId", projectId)
                    .addKeyValue("editingContextId", projectDocuments.editingContextId())
                    .setCause(exception)
                    .log();
            throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "The EMF document could not be read", exception);
        }
    }

    private ProjectDocuments getProjectDocuments(String projectId) {
        return this.projectDocumentsService.findByProjectId(projectId)
                .orElseThrow(() -> new RestfulEMFException(RestfulEMFError.NOT_FOUND, "Project not found"));
    }

}
