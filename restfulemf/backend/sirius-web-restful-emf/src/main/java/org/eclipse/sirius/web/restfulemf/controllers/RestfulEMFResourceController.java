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
package org.eclipse.sirius.web.restfulemf.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Semaphore;

import org.eclipse.sirius.web.application.capability.SiriusWebCapabilities;
import org.eclipse.sirius.web.application.capability.services.api.ICapabilityEvaluator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.sirius.web.restfulemf.application.api.IRestfulEMFReadApplicationService;
import org.eclipse.sirius.web.restfulemf.application.api.IRestfulEMFWriteApplicationService;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceRepresentation;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceWriteStatus;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.configuration.RestfulEMFProperties;
import org.eclipse.sirius.web.restfulemf.services.SizeLimitedInputStream;

/**
 * Exposes Sirius Web EMF documents through simple REST representations.
 */
@RestController
public class RestfulEMFResourceController {

    private final IRestfulEMFReadApplicationService readApplicationService;

    private final IRestfulEMFWriteApplicationService writeApplicationService;

    private final ICapabilityEvaluator capabilityEvaluator;

    private final boolean requireIfMatch;

    private final long maximumRequestSize;

    private final Semaphore transferPermits;

    public RestfulEMFResourceController(IRestfulEMFReadApplicationService readApplicationService, IRestfulEMFWriteApplicationService writeApplicationService,
            ICapabilityEvaluator capabilityEvaluator, RestfulEMFProperties properties) {
        this.readApplicationService = Objects.requireNonNull(readApplicationService);
        this.writeApplicationService = Objects.requireNonNull(writeApplicationService);
        this.capabilityEvaluator = Objects.requireNonNull(capabilityEvaluator);
        var nonNullProperties = Objects.requireNonNull(properties);
        this.requireIfMatch = nonNullProperties.requireIfMatch();
        this.maximumRequestSize = nonNullProperties.maxRequestSize().toBytes();
        this.transferPermits = new Semaphore(nonNullProperties.maxConcurrentTransfers(), true);
    }

    @GetMapping("/api/rest/projects/{projectId}/epackages/bin")
    public void getEPackages(@PathVariable String projectId, HttpServletResponse response) throws IOException {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        this.acquireTransferPermit();
        try {
            response.setContentType("application/octet-stream");
            this.readApplicationService.getEPackages(projectId).write(response.getOutputStream());
        } finally {
            this.transferPermits.release();
        }
    }

    @GetMapping("/api/rest/projects/{projectId}/documents")
    public Map<String, String> getDocuments(@PathVariable String projectId) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        return this.readApplicationService.getDocuments(projectId);
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/bin")
    public void getBinaryResource(@PathVariable String projectId, @PathVariable String documentName, HttpServletResponse response) throws IOException {
        this.getResource(projectId, documentName, ResourceFormat.BINARY, "\t", "application/octet-stream", response);
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/xmi")
    public void getXMIResource(@PathVariable String projectId, @PathVariable String documentName, HttpServletResponse response) throws IOException {
        this.getResource(projectId, documentName, ResourceFormat.XMI, "\t", "application/octet-stream", response);
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/xmi.zip")
    public void getZippedXMIResource(@PathVariable String projectId, @PathVariable String documentName, HttpServletResponse response) throws IOException {
        this.getResource(projectId, documentName, ResourceFormat.ZIPPED_XMI, "\t", "application/octet-stream", response);
    }

    @GetMapping("/api/rest/projects/{projectId}/{documentName}/csv")
    public void getCSVResource(@PathVariable String projectId, @PathVariable String documentName,
            @RequestParam(defaultValue = "\t", name = "sep") String separator, HttpServletResponse response) throws IOException {
        this.getResource(projectId, documentName, ResourceFormat.CSV, separator, "text/plain;charset=UTF-8", response);
    }

    @PutMapping("/api/rest/projects/{projectId}/{documentName}/xmi")
    public ResponseEntity<?> putXMIResource(HttpServletRequest request, @PathVariable String projectId, @PathVariable String documentName, @RequestHeader HttpHeaders headers) throws IOException {
        return this.replaceResource(projectId, documentName, ResourceFormat.XMI, request.getInputStream(), headers);
    }

    @PutMapping("/api/rest/projects/{projectId}/{documentName}/xmi.zip")
    public ResponseEntity<?> putZippedXMIResource(HttpServletRequest request, @PathVariable String projectId, @PathVariable String documentName, @RequestHeader HttpHeaders headers) throws IOException {
        return this.replaceResource(projectId, documentName, ResourceFormat.ZIPPED_XMI, request.getInputStream(), headers);
    }

    @PutMapping("/api/rest/projects/{projectId}/{documentName}/bin")
    public ResponseEntity<?> putBinaryResource(HttpServletRequest request, @PathVariable String projectId, @PathVariable String documentName, @RequestHeader HttpHeaders headers) throws IOException {
        return this.replaceResource(projectId, documentName, ResourceFormat.BINARY, request.getInputStream(), headers);
    }

    @PutMapping("/api/rest/projects/{projectId}/{documentName}/csv")
    public ResponseEntity<ProblemDetail> putCSVResource() {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, "CSV resources cannot be updated");
        problem.setProperty("code", HttpStatus.METHOD_NOT_ALLOWED.name());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).allow(HttpMethod.GET).body(problem);
    }

    private void getResource(String projectId, String documentName, ResourceFormat format, String separator, String contentType, HttpServletResponse response) throws IOException {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        this.acquireTransferPermit();
        try {
            ResourceRepresentation representation = this.readApplicationService.getResource(projectId, documentName, format, separator);
            response.setContentType(contentType);
            response.setHeader(HttpHeaders.ETAG, '"' + representation.revision() + '"');
            representation.writer().write(response.getOutputStream());
        } finally {
            this.transferPermits.release();
        }
    }

    private ResponseEntity<?> replaceResource(String projectId, String documentName, ResourceFormat format, InputStream content, HttpHeaders headers) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.EDIT);
        if (headers.getContentLength() > this.maximumRequestSize) {
            throw new RestfulEMFException(RestfulEMFError.PAYLOAD_TOO_LARGE, "The request payload is too large");
        }
        this.acquireTransferPermit();
        try {
            var limitedContent = new SizeLimitedInputStream(content, this.maximumRequestSize);
            var result = this.writeApplicationService.replaceResource(projectId, documentName, format, limitedContent, this.getExpectedRevisions(headers));
            if (result.status() == ResourceWriteStatus.CONFLICT) {
                ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.PRECONDITION_FAILED, "The document revision does not match If-Match");
                problem.setProperty("code", "REVISION_CONFLICT");
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).eTag(result.revision()).body(problem);
            }
            return ResponseEntity.ok().eTag(result.revision()).build();
        } finally {
            this.transferPermits.release();
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
        if (!this.capabilityEvaluator.hasCapability(SiriusWebCapabilities.PROJECT, projectId, capability)) {
            throw new RestfulEMFException(RestfulEMFError.CAPABILITY_DENIED, "The project capability is not granted");
        }
    }

    private void acquireTransferPermit() {
        if (!this.transferPermits.tryAcquire()) {
            throw new RestfulEMFException(RestfulEMFError.TRANSFER_CAPACITY_EXHAUSTED, "Too many concurrent resource transfers");
        }
    }
}
