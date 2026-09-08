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
import java.nio.charset.StandardCharsets;
import java.util.List;
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
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UriUtils;

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
import org.eclipse.sirius.web.restfulemf.services.ResourcePaths;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;

/**
 * Exposes Sirius Web EMF documents through simple REST representations.
 */
@RestController
public class RestfulEMFResourceController {

    private static final String DOCUMENT_PATTERN = "/api/rest/projects/{projectId}/documents/{format}/**";

    private final IRestfulEMFReadApplicationService readApplicationService;

    private final IRestfulEMFWriteApplicationService writeApplicationService;

    private final ICapabilityEvaluator capabilityEvaluator;

    private final ResourcePaths resourcePaths;

    private final boolean requireIfMatch;

    private final long maximumRequestSize;

    private final Semaphore transferPermits;

    public RestfulEMFResourceController(IRestfulEMFReadApplicationService readApplicationService, IRestfulEMFWriteApplicationService writeApplicationService,
            ICapabilityEvaluator capabilityEvaluator, RestfulEMFProperties properties, ResourcePaths resourcePaths) {
        this.readApplicationService = Objects.requireNonNull(readApplicationService);
        this.writeApplicationService = Objects.requireNonNull(writeApplicationService);
        this.capabilityEvaluator = Objects.requireNonNull(capabilityEvaluator);
        this.resourcePaths = Objects.requireNonNull(resourcePaths);
        var nonNullProperties = Objects.requireNonNull(properties);
        this.requireIfMatch = nonNullProperties.requireIfMatch();
        this.maximumRequestSize = nonNullProperties.maxRequestSize().toBytes();
        this.transferPermits = new Semaphore(nonNullProperties.maxConcurrentTransfers(), true);
    }

    @GetMapping("/api/rest/projects/{projectId}/epackages/{format}")
    public void getEPackages(@PathVariable String projectId, @PathVariable String format, HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        var resourceFormat = this.getFormat(format);
        if (resourceFormat != ResourceFormat.XMI && resourceFormat != ResourceFormat.BINARY) {
            throw new RestfulEMFException(RestfulEMFError.NOT_FOUND, "Unsupported metamodel format");
        }
        this.acquireTransferPermit();
        try {
            this.writeRepresentation(this.readApplicationService.getEPackages(projectId, resourceFormat), "application/octet-stream", request, response);
        } finally {
            this.transferPermits.release();
        }
    }

    @GetMapping("/api/rest/projects/{projectId}/documents")
    public List<ResourceDocument> getDocuments(@PathVariable String projectId) {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        return this.readApplicationService.getDocuments(projectId);
    }

    @GetMapping(DOCUMENT_PATTERN)
    public void getResource(@PathVariable String projectId, @PathVariable String format,
            @RequestParam(defaultValue = "\t", name = "sep") String separator, HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.VIEW);
        var resourceFormat = this.getFormat(format);
        var documentPath = this.getPath(request);
        this.acquireTransferPermit();
        try {
            ResourceRepresentation representation = this.readApplicationService.getResource(projectId, documentPath, resourceFormat, separator);
            this.writeRepresentation(representation, resourceFormat == ResourceFormat.CSV ? "text/plain;charset=UTF-8" : "application/octet-stream", request, response);
        } finally {
            this.transferPermits.release();
        }
    }

    @PutMapping(DOCUMENT_PATTERN)
    public ResponseEntity<?> putResource(@PathVariable String projectId, @PathVariable String format,
            HttpServletRequest request, @RequestHeader HttpHeaders headers) throws IOException {
        this.checkCapability(projectId, SiriusWebCapabilities.Project.EDIT);
        var resourceFormat = this.getFormat(format);
        var documentPath = this.getPath(request);
        if (resourceFormat == ResourceFormat.CSV) {
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, "CSV resources cannot be updated");
            problem.setProperty("code", HttpStatus.METHOD_NOT_ALLOWED.name());
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).allow(HttpMethod.GET, HttpMethod.HEAD).body(problem);
        }
        return this.replaceResource(projectId, documentPath, resourceFormat, request.getInputStream(), headers, request.getRequestURI());
    }

    private ResponseEntity<?> replaceResource(String projectId, String path, ResourceFormat format, InputStream content, HttpHeaders headers, String location) {
        if (headers.getContentLength() > this.maximumRequestSize) {
            throw new RestfulEMFException(RestfulEMFError.PAYLOAD_TOO_LARGE, "The request payload is too large");
        }
        this.acquireTransferPermit();
        try {
            var limitedContent = new SizeLimitedInputStream(content, this.maximumRequestSize);
            boolean createOnly = this.isCreateOnly(headers);
            var result = this.writeApplicationService.replaceResource(projectId, path, format, limitedContent, this.getExpectedRevisions(headers, createOnly), createOnly);
            if (result.status() == ResourceWriteStatus.CONFLICT) {
                ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.PRECONDITION_FAILED, "The document revision does not match If-Match");
                problem.setProperty("code", "REVISION_CONFLICT");
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(problem);
            }
            if (result.status() == ResourceWriteStatus.CREATED) {
                return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, location).build();
            }
            return ResponseEntity.noContent().build();
        } finally {
            this.transferPermits.release();
        }
    }

    private List<String> getExpectedRevisions(HttpHeaders headers, boolean createOnly) {
        List<String> entityTags;
        try {
            entityTags = headers.getIfMatch();
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid If-Match header", exception);
        }
        if (this.requireIfMatch && entityTags.isEmpty() && !createOnly) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED, "If-Match or If-None-Match: * is required");
        }
        return entityTags.stream()
                .map(entityTag -> entityTag.startsWith("\"") ? entityTag.substring(1, entityTag.length() - 1) : entityTag)
                .toList();
    }

    private boolean isCreateOnly(HttpHeaders headers) {
        String value = headers.getFirst(HttpHeaders.IF_NONE_MATCH);
        if (value != null && (!"*".equals(value.trim()) || headers.containsHeader(HttpHeaders.IF_MATCH))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use either If-Match or If-None-Match: *");
        }
        return value != null;
    }

    private ResourceFormat getFormat(String format) {
        return switch (format) {
            case "xmi" -> ResourceFormat.XMI;
            case "xmi.zip" -> ResourceFormat.ZIPPED_XMI;
            case "bin" -> ResourceFormat.BINARY;
            case "csv" -> ResourceFormat.CSV;
            default -> throw new RestfulEMFException(RestfulEMFError.NOT_FOUND, "Unsupported resource format");
        };
    }

    private String getPath(HttpServletRequest request) {
        String rawPath = request.getRequestURI().toLowerCase(java.util.Locale.ROOT);
        if (rawPath.contains("%2f") || rawPath.contains("%5c") || rawPath.contains("%25") || rawPath.contains(";") || rawPath.contains("//")) {
            throw new RestfulEMFException(RestfulEMFError.INVALID_RESOURCE, "Ambiguous document path encoding");
        }
        String lookupPath = request.getRequestURI().substring(request.getContextPath().length());
        String documentPath = UriUtils.decode(new AntPathMatcher().extractPathWithinPattern(DOCUMENT_PATTERN, lookupPath), StandardCharsets.UTF_8);
        this.resourcePaths.validate(documentPath);
        return documentPath;
    }

    private void writeRepresentation(ResourceRepresentation representation, String contentType, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(contentType);
        response.setHeader(HttpHeaders.ETAG, '"' + representation.revision() + '"');
        if (!HttpMethod.HEAD.matches(request.getMethod())) {
            representation.writer().write(response.getOutputStream());
        }
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
