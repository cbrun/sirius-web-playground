/*******************************************************************************
 * Copyright (c) 2019, 2026 Obeo.
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
package fr.obeo.playground.restfulemf.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.sirius.components.collaborative.api.IEditingContextEventProcessorRegistry;
import org.eclipse.sirius.components.core.api.ErrorPayload;
import org.eclipse.sirius.components.core.api.IEditingContextSearchService;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.emf.services.EObjectIDManager;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.emf.utils.EMFResourceUtils;
import org.eclipse.sirius.web.application.project.services.api.IProjectEditingContextService;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.Document;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.SemanticData;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.services.api.ISemanticDataSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import fr.obeo.playground.restfulemf.ReplaceResourceContentInput;
import fr.obeo.playground.restfulemf.SheetDataTable;

/**
 * Exposes Sirius Web EMF documents through simple REST representations.
 */
@RestController
public class RestfulEMFResourceController {

    private static final Duration EVENT_TIMEOUT = Duration.ofSeconds(10);

    private final IEditingContextSearchService editingContextSearchService;

    private final ISemanticDataSearchService semanticDataSearchService;

    private final IProjectEditingContextService projectEditingContextService;

    private final IEditingContextEventProcessorRegistry editingContextEventProcessorRegistry;

    private final List<EPackage> registeredPackages;

    private final Logger logger = LoggerFactory.getLogger(RestfulEMFResourceController.class);

    public RestfulEMFResourceController(IEditingContextSearchService editingContextSearchService, ISemanticDataSearchService semanticDataSearchService,
            IProjectEditingContextService projectEditingContextService, IEditingContextEventProcessorRegistry editingContextEventProcessorRegistry,
            List<EPackage> registeredPackages) {
        this.editingContextSearchService = Objects.requireNonNull(editingContextSearchService);
        this.semanticDataSearchService = Objects.requireNonNull(semanticDataSearchService);
        this.projectEditingContextService = Objects.requireNonNull(projectEditingContextService);
        this.editingContextEventProcessorRegistry = Objects.requireNonNull(editingContextEventProcessorRegistry);
        this.registeredPackages = List.copyOf(Objects.requireNonNull(registeredPackages));
    }

    @GetMapping("/api/rest/projects/{projectId}/epackages/bin")
    public byte[] getEPackages(@PathVariable String projectId) {
        this.getEditingContextId(projectId);
        XMLResource targetResource = new XMLResourceImpl(URI.createURI("sirius:///" + projectId + "/epackages"));
        var copier = new EcoreUtil.Copier();
        this.registeredPackages.forEach(ePackage -> targetResource.getContents().add(copier.copy(ePackage)));
        copier.copyReferences();

        return this.save(targetResource, Map.of(XMLResource.OPTION_BINARY, Boolean.TRUE), projectId, null);
    }

    @GetMapping("/api/rest/projects/{projectId}/documents")
    public Map<String, String> getDocuments(@PathVariable String projectId) {
        Map<String, String> documents = new LinkedHashMap<>();
        this.getSemanticData(projectId).getDocuments().forEach(document -> documents.put(document.getId().toString(), document.getName()));
        return documents;
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/bin")
    public byte[] getBinaryResource(@PathVariable String projectId, @PathVariable String documentName) {
        Resource resource = this.getResource(projectId, documentName);
        XMLResource targetResource = new XMLResourceImpl(resource.getURI());
        this.copyContents(resource, targetResource);
        return this.save(targetResource, Map.of(XMLResource.OPTION_BINARY, Boolean.TRUE), projectId, this.findDocument(projectId, documentName).getId());
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/xmi")
    public byte[] getXMIResource(@PathVariable String projectId, @PathVariable String documentName) {
        return this.getXMI(projectId, documentName, false);
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/xmi.zip")
    public byte[] getZippedXMIResource(@PathVariable String projectId, @PathVariable String documentName) {
        return this.getXMI(projectId, documentName, true);
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/csv")
    public String getCSVResource(@PathVariable String projectId, @PathVariable String documentName,
            @RequestParam(defaultValue = "\t", name = "sep") String separator) {
        Resource resource = this.getResource(projectId, documentName);
        var table = new SheetDataTable();
        var idManager = new EObjectIDManager();
        Set<EObject> ignoredObjects = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());

        resource.getAllContents().forEachRemaining(object -> {
            if (!ignoredObjects.contains(object)) {
                idManager.findId(object).ifPresent(id -> {
                    table.updateValue(id, "eClass", object.eClass().getName());
                    this.putAttributesInTable(table, object, id);
                    for (EReference containment : object.eClass().getEAllContainments()) {
                        if (containment.getUpperBound() == 1 && object.eGet(containment) instanceof EObject child) {
                            ignoredObjects.add(child);
                            table.updateValue(id, containment.getName(), child.eClass().getName());
                            this.putAttributesInTable(table, child, id);
                        }
                    }
                });
            }
        });

        table.fillEmptyCells();
        return table.getValues().stream()
                .map(line -> String.join(separator, line))
                .collect(java.util.stream.Collectors.joining("\n", "", "\n"));
    }

    @PutMapping("/api/rest/projects/{projectId}/{documentName}/xmi")
    public void putXMIResource(@RequestBody byte[] content, @PathVariable String projectId, @PathVariable String documentName) {
        this.putXMI(content, projectId, documentName, false);
    }

    @PutMapping("/api/rest/projects/{projectId}/{documentName}/xmi.zip")
    public void putZippedXMIResource(@RequestBody byte[] content, @PathVariable String projectId, @PathVariable String documentName) {
        this.putXMI(content, projectId, documentName, true);
    }

    @PutMapping("/api/rest/projects/{projectId}/{documentName}/bin")
    public void putBinaryResource(@RequestBody byte[] content, @PathVariable String projectId, @PathVariable String documentName) {
        Document document = this.getWritableDocument(projectId, documentName);
        XMLResource resource = new XMLResourceImpl(this.createURIForDocument(document));
        Map<String, Object> options = new HashMap<>();
        options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);
        this.loadAndReplace(content, projectId, document, resource, options);
    }

    private byte[] getXMI(String projectId, String documentName, boolean zipped) {
        Resource resource = this.getResource(projectId, documentName);
        XMIResource targetResource = new XMIResourceImpl(resource.getURI());
        this.copyContents(resource, targetResource);
        Map<String, Object> options = new HashMap<>(new EMFResourceUtils().getXMILoadOptions());
        if (zipped) {
            options.put(Resource.OPTION_ZIP, Boolean.TRUE);
        }
        return this.save(targetResource, options, projectId, this.findDocument(projectId, documentName).getId());
    }

    private void putXMI(byte[] content, String projectId, String documentName, boolean zipped) {
        Document document = this.getWritableDocument(projectId, documentName);
        XMIResource resource = new XMIResourceImpl(this.createURIForDocument(document));
        Map<String, Object> options = new HashMap<>(new EMFResourceUtils().getXMILoadOptions());
        if (zipped) {
            options.put(Resource.OPTION_ZIP, Boolean.TRUE);
        }
        this.loadAndReplace(content, projectId, document, resource, options);
    }

    private void loadAndReplace(byte[] content, String projectId, Document document, Resource resource, Map<String, Object> options) {
        try (var inputStream = new ByteArrayInputStream(content)) {
            resource.load(inputStream, options);
        } catch (IOException | RuntimeException exception) {
            this.logger.atWarn()
                    .setMessage("Document content could not be loaded")
                    .addKeyValue("projectId", projectId)
                    .addKeyValue("documentId", document.getId())
                    .setCause(exception)
                    .log();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid EMF document", exception);
        }

        String editingContextId = this.getEditingContextId(projectId);
        var input = new ReplaceResourceContentInput(UUID.randomUUID(), resource);
        try {
            IPayload payload = this.editingContextEventProcessorRegistry.dispatchEvent(editingContextId, input).block(EVENT_TIMEOUT);
            if (payload == null || payload instanceof ErrorPayload) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The EMF document could not be replaced");
            }
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            this.logger.atWarn()
                    .setMessage("Document replacement failed")
                    .addKeyValue("projectId", projectId)
                    .addKeyValue("editingContextId", editingContextId)
                    .addKeyValue("documentId", document.getId())
                    .setCause(exception)
                    .log();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The EMF document could not be replaced", exception);
        }
    }

    private void putAttributesInTable(SheetDataTable table, EObject object, String objectId) {
        for (EAttribute attribute : object.eClass().getEAllAttributes()) {
            Object value = object.eGet(attribute);
            if (!attribute.isMany() && value != null) {
                String serializedValue = attribute.getEType().getEPackage().getEFactoryInstance().convertToString((EDataType) attribute.getEType(), value);
                if (value instanceof String) {
                    serializedValue = "\"" + serializedValue.replace("\"", "\"\"") + "\"";
                }
                table.updateValue(objectId, attribute.getName(), serializedValue);
            }
        }
    }

    private byte[] save(Resource resource, Map<String, Object> options, String projectId, UUID documentId) {
        try (var outputStream = new ByteArrayOutputStream()) {
            resource.save(outputStream, options);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            var loggingEvent = this.logger.atWarn()
                    .setMessage("EMF resource serialization failed")
                    .addKeyValue("projectId", projectId)
                    .setCause(exception);
            if (documentId != null) {
                loggingEvent.addKeyValue("documentId", documentId);
            }
            loggingEvent.log();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The EMF resource could not be serialized", exception);
        }
    }

    private void copyContents(Resource sourceResource, XMLResource targetResource) {
        var copier = new EcoreUtil.Copier();
        targetResource.getContents().addAll(copier.copyAll(sourceResource.getContents()));
        copier.copyReferences();
        var idManager = new EObjectIDManager();
        copier.forEach((sourceObject, copiedObject) -> idManager.findId(sourceObject).ifPresent(id -> targetResource.setID(copiedObject, id)));
    }

    private Document getWritableDocument(String projectId, String documentName) {
        Document document = this.findDocument(projectId, documentName);
        if (document.isReadOnly()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The document is read-only");
        }
        this.getResource(projectId, documentName);
        return document;
    }

    private Document findDocument(String projectId, String documentName) {
        return this.getSemanticData(projectId).getDocuments().stream()
                .filter(document -> document.getName().equals(documentName) || document.getId().toString().equals(documentName))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
    }

    private SemanticData getSemanticData(String projectId) {
        String editingContextId = this.getEditingContextId(projectId);
        try {
            return this.semanticDataSearchService.findById(UUID.fromString(editingContextId))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project semantic data not found"));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project semantic data not found", exception);
        }
    }

    private Resource getResource(String projectId, String documentName) {
        Document document = this.findDocument(projectId, documentName);
        String editingContextId = this.getEditingContextId(projectId);
        URI documentURI = new JSONResourceFactory().createResourceURI(document.getId().toString());
        return this.editingContextSearchService.findById(editingContextId)
                .filter(IEMFEditingContext.class::isInstance)
                .map(IEMFEditingContext.class::cast)
                .flatMap(editingContext -> editingContext.getDomain().getResourceSet().getResources().stream()
                        .filter(resource -> resource.getURI().equals(documentURI))
                        .findFirst())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document resource not found"));
    }

    private String getEditingContextId(String projectId) {
        return this.projectEditingContextService.getEditingContextId(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    private URI createURIForDocument(Document document) {
        return new JSONResourceFactory().createResourceURI(document.getId().toString());
    }
}
