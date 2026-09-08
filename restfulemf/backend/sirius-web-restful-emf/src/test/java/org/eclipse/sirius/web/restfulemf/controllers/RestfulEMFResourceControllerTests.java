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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.eclipse.sirius.web.application.capability.services.api.ICapabilityEvaluator;
import org.eclipse.sirius.web.restfulemf.application.api.IRestfulEMFReadApplicationService;
import org.eclipse.sirius.web.restfulemf.application.api.IRestfulEMFWriteApplicationService;
import org.eclipse.sirius.web.restfulemf.application.api.IResourceWriter;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceRepresentation;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceWriteResult;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceWriteStatus;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.configuration.RestfulEMFProperties;
import org.eclipse.sirius.web.restfulemf.services.ResourcePaths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.unit.DataSize;
import org.springframework.web.server.ResponseStatusException;

/**
 * Verifies HTTP-specific response and precondition rules at the controller boundary.
 */
public class RestfulEMFResourceControllerTests {

    private static final String PROJECT = "project";

    private static final String PATH = "domain/model.ecore";

    private static final String ENDPOINT = "/api/rest/projects/project/documents/xmi/" + PATH;

    private static final String XMI = "xmi";

    @ParameterizedTest
    @ValueSource(strings = {"bin", "xmi.zip", "csv"})
    public void givenRepresentationsWhenGettingThenTheBodyIsWrittenAndThePermitIsReleased(String format) throws IOException {
        var read = mock(IRestfulEMFReadApplicationService.class);
        when(read.getResource(anyString(), anyString(), any(), anyString())).thenReturn(new ResourceRepresentation(output -> assertThatCode(() -> output.write(42)).doesNotThrowAnyException(), "tag"));
        var controller = this.controller(read, mock(IRestfulEMFWriteApplicationService.class), false);
        var request = new MockHttpServletRequest("GET", ENDPOINT.replace(XMI, format));
        var response = new MockHttpServletResponse();
        controller.getResource(PROJECT, format, "\t", request, response);
        assertThat(response.getContentAsByteArray()).containsExactly((byte) 42);
        controller.getResource(PROJECT, format, "\t", request, new MockHttpServletResponse());
        if (format.equals("csv")) {
            assertThat(response.getContentType()).isEqualTo("text/plain;charset=UTF-8");
            assertThat(controller.putResource(PROJECT, format, request, new HttpHeaders()).getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @Test
    public void givenMetamodelsAndDocumentsWhenGettingThenApplicationResultsAreReturned() throws IOException {
        var read = mock(IRestfulEMFReadApplicationService.class);
        when(read.getDocuments(PROJECT)).thenReturn(List.of());
        when(read.getEPackages(PROJECT, ResourceFormat.BINARY)).thenReturn(new ResourceRepresentation(output -> assertThatCode(() -> output.write(7)).doesNotThrowAnyException(), "metamodel"));
        var controller = this.controller(read, mock(IRestfulEMFWriteApplicationService.class), false);
        assertThat(controller.getDocuments(PROJECT)).isEmpty();
        var response = new MockHttpServletResponse();
        controller.getEPackages(PROJECT, "bin", new MockHttpServletRequest("GET", ENDPOINT), response);
        assertThat(response.getContentAsByteArray()).containsExactly((byte) 7);
        assertThatThrownBy(() -> controller.getEPackages(PROJECT, "csv", new MockHttpServletRequest(), response)).isInstanceOf(RestfulEMFException.class);
        assertThatThrownBy(() -> controller.getEPackages(PROJECT, "invalid", new MockHttpServletRequest(), response)).isInstanceOf(RestfulEMFException.class);
    }

    @ParameterizedTest
    @EnumSource(value = ResourceWriteStatus.class, names = {"UPDATED", "CONFLICT"})
    public void givenConditionalReplacementWhenWritingThenTheHttpStatusMatchesTheOutcome(ResourceWriteStatus status) throws IOException {
        var write = mock(IRestfulEMFWriteApplicationService.class);
        when(write.replaceResource(anyString(), anyString(), any(), any(), anyList(), anyBoolean())).thenReturn(new ResourceWriteResult(status, "current"));
        var headers = new HttpHeaders();
        headers.setIfMatch(List.of("\"old\"", "*"));
        var response = this.controller(mock(IRestfulEMFReadApplicationService.class), write, false)
                .putResource(PROJECT, XMI, new MockHttpServletRequest("PUT", ENDPOINT), headers);
        assertThat(response.getStatusCode()).isEqualTo(status == ResourceWriteStatus.UPDATED ? HttpStatus.NO_CONTENT : HttpStatus.PRECONDITION_FAILED);
        assertThat(response.getHeaders().getETag()).isNull();
    }

    @Test
    public void givenInvalidConditionsOrOversizedBodyWhenWritingThenApplicationIsNotCalled() {
        var write = mock(IRestfulEMFWriteApplicationService.class);
        var controller = this.controller(mock(IRestfulEMFReadApplicationService.class), write, false);
        var request = new MockHttpServletRequest("PUT", ENDPOINT);
        var headers = new HttpHeaders();
        headers.setIfNoneMatch("\"not-star\"");
        assertThatThrownBy(() -> controller.putResource(PROJECT, XMI, request, headers)).isInstanceOf(ResponseStatusException.class);
        headers.setIfNoneMatch("*");
        headers.setIfMatch("*");
        assertThatThrownBy(() -> controller.putResource(PROJECT, XMI, request, headers)).isInstanceOf(ResponseStatusException.class);
        headers.clear();
        headers.setContentLength(DataSize.ofMegabytes(2).toBytes());
        assertThatThrownBy(() -> controller.putResource(PROJECT, XMI, request, headers)).isInstanceOfSatisfying(RestfulEMFException.class,
                exception -> assertThat(exception.getError()).isEqualTo(RestfulEMFError.PAYLOAD_TOO_LARGE));
        verifyNoInteractions(write);
    }

    @Test
    public void givenDeniedCapabilityWhenListingThenNoApplicationAccessOccurs() {
        var read = mock(IRestfulEMFReadApplicationService.class);
        var properties = new RestfulEMFProperties(false, DataSize.ofBytes(1), DataSize.ofBytes(1), 1);
        var controller = new RestfulEMFResourceController(read, mock(IRestfulEMFWriteApplicationService.class), mock(ICapabilityEvaluator.class), properties, new ResourcePaths());
        assertThatThrownBy(() -> controller.getDocuments(PROJECT)).isInstanceOfSatisfying(RestfulEMFException.class,
                exception -> assertThat(exception.getError()).isEqualTo(RestfulEMFError.CAPABILITY_DENIED));
        verifyNoInteractions(read);
    }

    @Test
    public void givenActiveTransferWhenAnotherStartsThenCapacityIsEnforced() throws IOException {
        var read = mock(IRestfulEMFReadApplicationService.class);
        var controller = this.controller(read, mock(IRestfulEMFWriteApplicationService.class), false);
        var request = new MockHttpServletRequest("GET", ENDPOINT);
        when(read.getResource(anyString(), anyString(), any(), anyString())).thenReturn(new ResourceRepresentation(output -> {
            assertThatThrownBy(() -> controller.getResource(PROJECT, XMI, "\t", request, new MockHttpServletResponse()))
                    .isInstanceOfSatisfying(RestfulEMFException.class, exception -> assertThat(exception.getError()).isEqualTo(RestfulEMFError.TRANSFER_CAPACITY_EXHAUSTED));
        }, "capacity"));
        controller.getResource(PROJECT, XMI, "\t", request, new MockHttpServletResponse());
    }

    @Test
    public void givenHeadWhenReadingThenTheTagIsReturnedWithoutSerializingTheBody() throws IOException {
        var read = mock(IRestfulEMFReadApplicationService.class);
        var writer = mock(IResourceWriter.class);
        when(read.getResource(PROJECT, PATH, ResourceFormat.XMI, "\t")).thenReturn(new ResourceRepresentation(writer, "revision"));
        var response = new MockHttpServletResponse();
        this.controller(read, mock(IRestfulEMFWriteApplicationService.class), false)
                .getResource(PROJECT, XMI, "\t", new MockHttpServletRequest("HEAD", ENDPOINT), response);
        assertThat(response.getHeader(HttpHeaders.ETAG)).isEqualTo("\"revision\"");
        assertThat(response.getContentAsByteArray()).isEmpty();
        verifyNoInteractions(writer);
    }

    @Test
    public void givenCreateOnlyWhenWritingThenTheConditionIsForwardedAndNoTagIsReturned() throws IOException {
        var write = mock(IRestfulEMFWriteApplicationService.class);
        when(write.replaceResource(anyString(), anyString(), any(), any(), anyList(), anyBoolean()))
                .thenReturn(new ResourceWriteResult(ResourceWriteStatus.CREATED, ""));
        var headers = new HttpHeaders();
        headers.setIfNoneMatch("*");
        var response = this.controller(mock(IRestfulEMFReadApplicationService.class), write, true)
                .putResource(PROJECT, XMI, new MockHttpServletRequest("PUT", ENDPOINT), headers);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).hasToString(ENDPOINT);
        assertThat(response.getHeaders().getETag()).isNull();
        verify(write).replaceResource(org.mockito.ArgumentMatchers.eq(PROJECT), org.mockito.ArgumentMatchers.eq(PATH),
                org.mockito.ArgumentMatchers.eq(ResourceFormat.XMI), any(), org.mockito.ArgumentMatchers.eq(List.of()), org.mockito.ArgumentMatchers.eq(true));
    }

    @Test
    public void givenStrictModeWhenConditionIsMissingThenTheWriteIsRejected() {
        var write = mock(IRestfulEMFWriteApplicationService.class);
        var controller = this.controller(mock(IRestfulEMFReadApplicationService.class), write, true);
        assertThatThrownBy(() -> controller.putResource(PROJECT, XMI, new MockHttpServletRequest("PUT", ENDPOINT), new HttpHeaders()))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception -> assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_REQUIRED));
        verifyNoInteractions(write);
    }

    @ParameterizedTest
    @ValueSource(strings = {"folder%2fmodel.ecore", "folder%5cmodel.ecore", "model%2520.ecore", "folder;ignored/model.ecore"})
    public void givenAmbiguousEncodedPathWhenReadingThenTheRequestIsRejected(String encodedPath) {
        var read = mock(IRestfulEMFReadApplicationService.class);
        var controller = this.controller(read, mock(IRestfulEMFWriteApplicationService.class), false);
        assertThatThrownBy(() -> controller.getResource(PROJECT, XMI, "\t",
                new MockHttpServletRequest("GET", "/api/rest/projects/project/documents/xmi/" + encodedPath), new MockHttpServletResponse()))
                .isInstanceOf(RestfulEMFException.class);
        verifyNoInteractions(read);
    }

    private RestfulEMFResourceController controller(IRestfulEMFReadApplicationService read, IRestfulEMFWriteApplicationService write, boolean strict) {
        var capabilities = mock(ICapabilityEvaluator.class);
        when(capabilities.hasCapability(anyString(), anyString(), anyString())).thenReturn(true);
        var properties = new RestfulEMFProperties(strict, DataSize.ofMegabytes(1), DataSize.ofMegabytes(1), 1);
        return new RestfulEMFResourceController(read, write, capabilities, properties, new ResourcePaths());
    }
}
