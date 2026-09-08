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

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Verifies stable problem codes and HTTP error status mapping.
 */
public class RestfulEMFExceptionHandlerTests {

    @ParameterizedTest
    @CsvSource({"CAPABILITY_DENIED,403", "READ_ONLY,403", "NOT_FOUND,404", "CONFLICT,409", "INVALID_RESOURCE,400", "PAYLOAD_TOO_LARGE,413",
            "TRANSFER_CAPACITY_EXHAUSTED,503", "TIMEOUT,504", "PROCESSING_FAILURE,500"})
    public void givenApplicationErrorWhenMappingThenStatusAndCodeArePreserved(RestfulEMFError error, int status) {
        var response = new RestfulEMFExceptionHandler().handleRestfulEMFException(new RestfulEMFException(error, "detail"));
        assertThat(response.getStatusCode().value()).isEqualTo(status);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).isEqualTo("detail");
        assertThat(response.getBody().getProperties()).containsEntry("code", error.name());
        assertThat(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER)).isEqualTo(error == RestfulEMFError.TRANSFER_CAPACITY_EXHAUSTED ? "1" : null);
    }

    @Test
    public void givenResponseStatusExceptionWhenMappingThenTheProblemIsRetained() {
        var response = new RestfulEMFExceptionHandler().handleResponseStatusException(new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED, "condition required"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_REQUIRED);
        assertThat(response.getBody().getProperties()).containsEntry("code", HttpStatus.PRECONDITION_REQUIRED.name());
    }
}

