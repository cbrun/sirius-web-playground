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
import java.nio.charset.StandardCharsets;
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
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.sirius.components.core.api.ErrorPayload;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.emf.services.EObjectIDManager;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.utils.EMFResourceUtils;
import org.eclipse.sirius.components.graphql.api.IEditingContextDispatcher;
import org.eclipse.sirius.web.application.capability.SiriusWebCapabilities;
import org.eclipse.sirius.web.application.capability.services.api.ICapabilityEvaluator;
import org.eclipse.sirius.web.application.project.services.api.IProjectEditingContextService;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.Document;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.SemanticData;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.services.api.ISemanticDataSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import fr.obeo.playground.restfulemf.GetResourceContentInput;
import fr.obeo.playground.restfulemf.GetResourceContentSuccessPayload;
import fr.obeo.playground.restfulemf.IResourceSnapshotService;
import fr.obeo.playground.restfulemf.ReplaceResourceContentInput;
import fr.obeo.playground.restfulemf.ReplaceResourceContentSuccessPayload;
import fr.obeo.playground.restfulemf.ResourceRevisionConflictPayload;
import fr.obeo.playground.restfulemf.ResourceSnapshot;
import fr.obeo.playground.restfulemf.SheetDataTable;

/**
 * Exposes Sirius Web EMF documents through simple REST representations.
 */
@RestController
public class RestfulEMFResourceController {

    private static final Duration EVENT_TIMEOUT = Duration.ofSeconds(10);

    private final ISemanticDataSearchService semanticDataSearchService;

    private final IProjectEditingContextService projectEditingContextService;

    private final IEditingContextDispatcher editingContextDispatcher;

    private final ICapabilityEvaluator capabilityEvaluator;

    private final IResourceSnapshotService resourceSnapshotService;

    private final List<EPackage> registeredPackages;

    private final boolean requireIfMatch;

    private final Logger logger = LoggerFactory.getLogger(RestfulEMFResourceController.class);

    public RestfulEMFResourceController(ISemanticDataSearchService semanticDataSearchService, IProjectEditingContextService projectEditingContextService,
            IEditingContextDispatcher editingContextDispatcher, ICapabilityEvaluator capabilityEvaluator, IResourceSnapshotService resourceSnapshotService,
            List<EPackage> registeredPackages, @Value("${sirius.web.restfulemf.require-if-match:false}") boolean requireIfMatch) {
        this.semanticDataSearchService = Objects.requireNonNull(semanticDataSearchService);
        this.projectEditingContextService = Objects.requireNonNull(projectEditingContextService);
        this.editingContextDispatcher = Objects.requireNonNull(editingContextDispatcher);
        this.capabilityEvaluator = Objects.requireNonNull(capabilityEvaluator);
        this.resourceSnapshotService = Objects.requireNonNull(resourceSnapshotService);
        this.registeredPackages = List.copyOf(Objects.requireNonNull(registeredPackages));
        this.requireIfMatch = requireIfMatch;
    }

    @GetMapping("/api/rest/projects/{projectId}/epackages/bin")
    public byte[] getEPackages(@PathVariable String projectId) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        XMLResource targetResource = new XMLResourceImpl(URI.createURI("sirius:///" + projectId + "/epackages"));
        var copier = new EcoreUtil.Copier();
        this.registeredPackages.forEach(ePackage -> targetResource.getContents().add(copier.copy(ePackage)));
        copier.copyReferences();

        return this.save(targetResource, Map.of(XMLResource.OPTION_BINARY, Boolean.TRUE), projectId, null);
    }

    @GetMapping("/api/rest/projects/{projectId}/documents")
    public Map<String, String> getDocuments(@PathVariable String projectId) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        Map<String, String> documents = new LinkedHashMap<>();
        this.getSemanticData(projectId).getDocuments().forEach(document -> documents.put(document.getId().toString(), document.getName()));
        return documents;
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/bin")
    public ResponseEntity<byte[]> getBinaryResource(@PathVariable String projectId, @PathVariable String documentName) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        ResourceSnapshot snapshot = this.getResourceSnapshot(projectId, documentName);
        Resource resource = this.loadSnapshot(projectId, documentName, snapshot);
        XMLResource targetResource = new XMLResourceImpl(resource.getURI());
        this.copyContents(resource, targetResource);
        byte[] content = this.save(targetResource, Map.of(XMLResource.OPTION_BINARY, Boolean.TRUE), projectId, this.findDocument(projectId, documentName).getId());
        return ResponseEntity.ok().eTag(snapshot.revision()).body(content);
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/xmi")
    public ResponseEntity<byte[]> getXMIResource(@PathVariable String projectId, @PathVariable String documentName) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        return this.getXMI(projectId, documentName, false);
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/xmi.zip")
    public ResponseEntity<byte[]> getZippedXMIResource(@PathVariable String projectId, @PathVariable String documentName) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        return this.getXMI(projectId, documentName, true);
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/csv")
    public ResponseEntity<String> getCSVResource(@PathVariable String projectId, @PathVariable String documentName,
            @RequestParam(defaultValue = "\t", name = "sep") String separator) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        ResourceSnapshot snapshot = this.getResourceSnapshot(projectId, documentName);
        Resource resource = this.loadSnapshot(projectId, documentName, snapshot);
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
        String content = table.getValues().stream()
                .map(line -> String.join(separator, line))
                .collect(java.util.stream.Collectors.joining("\n", "", "\n"));
        return ResponseEntity.ok().eTag(snapshot.revision()).body(content);
    }

    @PutMapping("/api/rest/projects/{projectId}/{documentName}/xmi")
    public ResponseEntity<Void> putXMIResource(@RequestBody byte[] content, @PathVariable String projectId, @PathVariable String documentName, @RequestHeader HttpHeaders headers) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.EDIT);
        return this.putXMI(content, projectId, documentName, false, this.getExpectedRevisions(headers));
    }

    @PutMapping("/api/rest/projects/{projectId}/{documentName}/xmi.zip")
    public ResponseEntity<Void> putZippedXMIResource(@RequestBody byte[] content, @PathVariable String projectId, @PathVariable String documentName, @RequestHeader HttpHeaders headers) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.EDIT);
        return this.putXMI(content, projectId, documentName, true, this.getExpectedRevisions(headers));
    }

    @PutMapping("/api/rest/projects/{projectId}/{documentName}/bin")
    public ResponseEntity<Void> putBinaryResource(@RequestBody byte[] content, @PathVariable String projectId, @PathVariable String documentName, @RequestHeader HttpHeaders headers) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.EDIT);
        Document document = this.getWritableDocument(projectId, documentName);
        XMLResource resource = new XMLResourceImpl(this.createURIForDocument(document));
        Map<String, Object> options = new HashMap<>();
        options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);
        return this.loadAndReplace(content, projectId, document, resource, options, this.getExpectedRevisions(headers));
    }

    private ResponseEntity<byte[]> getXMI(String projectId, String documentName, boolean zipped) {
        ResourceSnapshot snapshot = this.getResourceSnapshot(projectId, documentName);
        Resource resource = this.loadSnapshot(projectId, documentName, snapshot);
        XMIResource targetResource = new XMIResourceImpl(resource.getURI());
        this.copyContents(resource, targetResource);
        Map<String, Object> options = new HashMap<>(new EMFResourceUtils().getXMILoadOptions());
        if (zipped) {
            options.put(Resource.OPTION_ZIP, Boolean.TRUE);
        }
        byte[] content = this.save(targetResource, options, projectId, this.findDocument(projectId, documentName).getId());
        return ResponseEntity.ok().eTag(snapshot.revision()).body(content);
    }

    private ResponseEntity<Void> putXMI(byte[] content, String projectId, String documentName, boolean zipped, List<String> expectedRevisions) {
        Document document = this.getWritableDocument(projectId, documentName);
        XMIResource resource = new XMIResourceImpl(this.createURIForDocument(document));
        Map<String, Object> options = new HashMap<>(new EMFResourceUtils().getXMILoadOptions());
        if (zipped) {
            options.put(Resource.OPTION_ZIP, Boolean.TRUE);
        }
        return this.loadAndReplace(content, projectId, document, resource, options, expectedRevisions);
    }

    private ResponseEntity<Void> loadAndReplace(byte[] content, String projectId, Document document, Resource resource, Map<String, Object> options, List<String> expectedRevisions) {
        ResourceSnapshot newSnapshot;
        try (var inputStream = new ByteArrayInputStream(content)) {
            resource.load(inputStream, options);
            newSnapshot = this.resourceSnapshotService.getSnapshot(resource)
                    .orElseThrow(() -> new IllegalArgumentException("The EMF resource could not be serialized"));
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
        var input = new ReplaceResourceContentInput(UUID.randomUUID(), document.getId().toString(), newSnapshot.content(), expectedRevisions);
        try {
            IPayload payload = this.editingContextDispatcher.dispatchMutation(editingContextId, input).block(EVENT_TIMEOUT);
            if (payload instanceof ReplaceResourceContentSuccessPayload successPayload) {
                return ResponseEntity.ok().eTag(successPayload.revision()).build();
            } else if (payload instanceof ResourceRevisionConflictPayload conflictPayload) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).eTag(conflictPayload.currentRevision()).build();
            } else if (payload == null || payload instanceof ErrorPayload) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The EMF document could not be replaced");
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected document replacement result");
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

    private ResourceSnapshot getResourceSnapshot(String projectId, String documentName) {
        Document document = this.findDocument(projectId, documentName);
        String editingContextId = this.getEditingContextId(projectId);
        var input = new GetResourceContentInput(UUID.randomUUID(), document.getId().toString());
        IPayload payload = this.editingContextDispatcher.dispatchQuery(editingContextId, input).block(EVENT_TIMEOUT);
        if (payload instanceof GetResourceContentSuccessPayload successPayload) {
            return successPayload.snapshot();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document resource not found");
    }

    private Resource loadSnapshot(String projectId, String documentName, ResourceSnapshot snapshot) {
        Document document = this.findDocument(projectId, documentName);
        var resourceSet = new ResourceSetImpl();
        this.registeredPackages.forEach(ePackage -> resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage));
        Resource resource = new JSONResourceFactory().createResource(this.createURIForDocument(document));
        resourceSet.getResources().add(resource);
        try (var inputStream = new ByteArrayInputStream(snapshot.content().getBytes(StandardCharsets.UTF_8))) {
            resource.load(inputStream, Map.of());
            return resource;
        } catch (IOException | RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The EMF resource snapshot could not be loaded", exception);
        }
    }

    private List<String> getExpectedRevisions(HttpHeaders headers) {
        List<String> entityTags;
        try {
            entityTags = headers.getIfMatch();
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid If-Match header", exception);
        }
        if (this.requireIfMatch && entityTags.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED, "An If-Match header is required");
        }
        return entityTags.stream()
                .map(entityTag -> entityTag.startsWith("\"") ? entityTag.substring(1, entityTag.length() - 1) : entityTag)
                .toList();
    }

    private void checkCapability(String projectId, String capability) {
        this.getEditingContextId(projectId);
        if (!this.capabilityEvaluator.hasCapability(SiriusWebCapabilities.PROJECT, projectId, capability)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The project capability is not granted");
        }
    }

    private String getEditingContextId(String projectId) {
        return this.projectEditingContextService.getEditingContextId(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    private URI createURIForDocument(Document document) {
        return new JSONResourceFactory().createResourceURI(document.getId().toString());
    }
}
