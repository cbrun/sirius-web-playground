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
package org.eclipse.sirius.web.restfulemf.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;

/**
 * Prevents REST errors from being converted to the Sirius Web frontend fallback page.
 */
@RestControllerAdvice(assignableTypes = RestfulEMFResourceController.class)
public class RestfulEMFExceptionHandler {

    @ExceptionHandler(RestfulEMFException.class)
    public ResponseEntity<ProblemDetail> handleRestfulEMFException(RestfulEMFException exception) {
        var status = switch (exception.getError()) {
            case CAPABILITY_DENIED, READ_ONLY -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_RESOURCE -> HttpStatus.BAD_REQUEST;
            case PAYLOAD_TOO_LARGE -> HttpStatus.PAYLOAD_TOO_LARGE;
            case TRANSFER_CAPACITY_EXHAUSTED -> HttpStatus.SERVICE_UNAVAILABLE;
            case TIMEOUT -> HttpStatus.GATEWAY_TIMEOUT;
            case PROCESSING_FAILURE -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        problem.setProperty("code", exception.getError().name());
        var response = ResponseEntity.status(status);
        if (exception.getError() == RestfulEMFError.TRANSFER_CAPACITY_EXHAUSTED) {
            response.header(HttpHeaders.RETRY_AFTER, "1");
        }
        return response.body(problem);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatusException(ResponseStatusException exception) {
        ProblemDetail problem = exception.getBody();
        if (exception.getStatusCode() instanceof HttpStatus status) {
            problem.setProperty("code", status.name());
        }
        return ResponseEntity.status(exception.getStatusCode()).body(problem);
    }
}
