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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.graphql.api.IEditingContextDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.obeo.playground.restfulemf.GetResourceContentInput;
import fr.obeo.playground.restfulemf.GetResourceContentSuccessPayload;
import fr.obeo.playground.restfulemf.application.api.IRestfulEMFReadApplicationService;
import fr.obeo.playground.restfulemf.application.api.IResourceWriter;
import fr.obeo.playground.restfulemf.application.api.ResourceFormat;
import fr.obeo.playground.restfulemf.application.api.ResourceRepresentation;
import fr.obeo.playground.restfulemf.application.api.RestfulEMFError;
import fr.obeo.playground.restfulemf.application.api.RestfulEMFException;
import fr.obeo.playground.restfulemf.services.api.IProjectDocumentsService;
import fr.obeo.playground.restfulemf.services.api.IResourceFormatService;
import fr.obeo.playground.restfulemf.services.api.ProjectDocuments;
import fr.obeo.playground.restfulemf.services.api.ResourceDocument;

/**
 * Orchestrates RESTful EMF read use cases.
 */
@Service
public class RestfulEMFReadApplicationService implements IRestfulEMFReadApplicationService {

    private static final Duration EVENT_TIMEOUT = Duration.ofSeconds(10);

    private final IProjectDocumentsService projectDocumentsService;

    private final IEditingContextDispatcher editingContextDispatcher;

    private final IResourceFormatService resourceFormatService;

    private final Logger logger = LoggerFactory.getLogger(RestfulEMFReadApplicationService.class);

    public RestfulEMFReadApplicationService(IProjectDocumentsService projectDocumentsService, IEditingContextDispatcher editingContextDispatcher,
            IResourceFormatService resourceFormatService) {
        this.projectDocumentsService = Objects.requireNonNull(projectDocumentsService);
        this.editingContextDispatcher = Objects.requireNonNull(editingContextDispatcher);
        this.resourceFormatService = Objects.requireNonNull(resourceFormatService);
    }

    @Override
    public Map<String, String> getDocuments(String projectId) {
        Map<String, String> documents = new LinkedHashMap<>();
        this.getProjectDocuments(projectId).documents().forEach(document -> documents.put(document.id().toString(), document.name()));
        return documents;
    }

    @Override
    public IResourceWriter getEPackages(String projectId) {
        this.getProjectDocuments(projectId);
        return outputStream -> this.resourceFormatService.serializeEPackages(projectId, outputStream);
    }

    @Override
    public ResourceRepresentation getResource(String projectId, String documentSelector, ResourceFormat format, String separator) {
        ProjectDocuments projectDocuments = this.getProjectDocuments(projectId);
        ResourceDocument document = this.findDocument(projectDocuments, documentSelector);
        var input = new GetResourceContentInput(UUID.randomUUID(), document.id().toString());
        try {
            IPayload payload = this.editingContextDispatcher.dispatchQuery(projectDocuments.editingContextId(), input)
                    .timeout(EVENT_TIMEOUT)
                    .onErrorMap(TimeoutException.class, exception -> new RestfulEMFException(RestfulEMFError.TIMEOUT, "The EMF document read timed out", exception))
                    .block();
            if (payload instanceof GetResourceContentSuccessPayload successPayload) {
                IResourceWriter writer = outputStream -> this.resourceFormatService.serialize(successPayload.snapshot(), document, format, separator, outputStream);
                return new ResourceRepresentation(writer, successPayload.snapshot().revision());
            }
            throw new RestfulEMFException(RestfulEMFError.NOT_FOUND, "Document resource not found");
        } catch (RestfulEMFException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            this.logger.atWarn()
                    .setMessage("Document resource query failed")
                    .addKeyValue("projectId", projectId)
                    .addKeyValue("editingContextId", projectDocuments.editingContextId())
                    .addKeyValue("documentId", document.id())
                    .setCause(exception)
                    .log();
            throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "The EMF document could not be read", exception);
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
