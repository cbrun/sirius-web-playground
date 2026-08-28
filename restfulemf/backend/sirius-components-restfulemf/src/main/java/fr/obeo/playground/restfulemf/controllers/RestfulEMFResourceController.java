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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.sirius.web.application.capability.SiriusWebCapabilities;
import org.eclipse.sirius.web.application.capability.services.api.ICapabilityEvaluator;
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

import fr.obeo.playground.restfulemf.application.api.IRestfulEMFReadApplicationService;
import fr.obeo.playground.restfulemf.application.api.IRestfulEMFWriteApplicationService;
import fr.obeo.playground.restfulemf.application.api.ResourceFormat;
import fr.obeo.playground.restfulemf.application.api.ResourceRepresentation;
import fr.obeo.playground.restfulemf.application.api.ResourceWriteStatus;

/**
 * Exposes Sirius Web EMF documents through simple REST representations.
 */
@RestController
public class RestfulEMFResourceController {

    private final IRestfulEMFReadApplicationService readApplicationService;

    private final IRestfulEMFWriteApplicationService writeApplicationService;

    private final ICapabilityEvaluator capabilityEvaluator;

    private final boolean requireIfMatch;

    public RestfulEMFResourceController(IRestfulEMFReadApplicationService readApplicationService, IRestfulEMFWriteApplicationService writeApplicationService,
            ICapabilityEvaluator capabilityEvaluator, @Value("${sirius.web.restfulemf.require-if-match:false}") boolean requireIfMatch) {
        this.readApplicationService = Objects.requireNonNull(readApplicationService);
        this.writeApplicationService = Objects.requireNonNull(writeApplicationService);
        this.capabilityEvaluator = Objects.requireNonNull(capabilityEvaluator);
        this.requireIfMatch = requireIfMatch;
    }

    @GetMapping("/api/rest/projects/{projectId}/epackages/bin")
    public byte[] getEPackages(@PathVariable String projectId) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        return this.readApplicationService.getEPackages(projectId);
    }

    @GetMapping("/api/rest/projects/{projectId}/documents")
    public Map<String, String> getDocuments(@PathVariable String projectId) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        return this.readApplicationService.getDocuments(projectId);
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/bin")
    public ResponseEntity<byte[]> getBinaryResource(@PathVariable String projectId, @PathVariable String documentName) {
        return this.getResource(projectId, documentName, ResourceFormat.BINARY);
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/xmi")
    public ResponseEntity<byte[]> getXMIResource(@PathVariable String projectId, @PathVariable String documentName) {
        return this.getResource(projectId, documentName, ResourceFormat.XMI);
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/xmi.zip")
    public ResponseEntity<byte[]> getZippedXMIResource(@PathVariable String projectId, @PathVariable String documentName) {
        return this.getResource(projectId, documentName, ResourceFormat.ZIPPED_XMI);
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/csv")
    public ResponseEntity<String> getCSVResource(@PathVariable String projectId, @PathVariable String documentName,
            @RequestParam(defaultValue = "\t", name = "sep") String separator) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        ResourceRepresentation representation = this.readApplicationService.getResource(projectId, documentName, ResourceFormat.CSV, separator);
        return ResponseEntity.ok().eTag(representation.revision()).body(new String(representation.content(), StandardCharsets.UTF_8));
    }

    @PutMapping("/api/rest/projects/{projectId}/{documentName}/xmi")
    public ResponseEntity<Void> putXMIResource(@RequestBody byte[] content, @PathVariable String projectId, @PathVariable String documentName, @RequestHeader HttpHeaders headers) {
        return this.replaceResource(projectId, documentName, ResourceFormat.XMI, content, headers);
    }

    @PutMapping("/api/rest/projects/{projectId}/{documentName}/xmi.zip")
    public ResponseEntity<Void> putZippedXMIResource(@RequestBody byte[] content, @PathVariable String projectId, @PathVariable String documentName, @RequestHeader HttpHeaders headers) {
        return this.replaceResource(projectId, documentName, ResourceFormat.ZIPPED_XMI, content, headers);
    }

    @PutMapping("/api/rest/projects/{projectId}/{documentName}/bin")
    public ResponseEntity<Void> putBinaryResource(@RequestBody byte[] content, @PathVariable String projectId, @PathVariable String documentName, @RequestHeader HttpHeaders headers) {
        return this.replaceResource(projectId, documentName, ResourceFormat.BINARY, content, headers);
    }

    private ResponseEntity<byte[]> getResource(String projectId, String documentName, ResourceFormat format) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        ResourceRepresentation representation = this.readApplicationService.getResource(projectId, documentName, format, "\t");
        return ResponseEntity.ok().eTag(representation.revision()).body(representation.content());
    }

    private ResponseEntity<Void> replaceResource(String projectId, String documentName, ResourceFormat format, byte[] content, HttpHeaders headers) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.EDIT);
        var result = this.writeApplicationService.replaceResource(projectId, documentName, format, content, this.getExpectedRevisions(headers));
        if (result.status() == ResourceWriteStatus.CONFLICT) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).eTag(result.revision()).build();
        }
        return ResponseEntity.ok().eTag(result.revision()).build();
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
        if (!this.capabilityEvaluator.hasCapability(SiriusWebCapabilities.PROJECT, projectId, capability)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The project capability is not granted");
        }
    }
}
