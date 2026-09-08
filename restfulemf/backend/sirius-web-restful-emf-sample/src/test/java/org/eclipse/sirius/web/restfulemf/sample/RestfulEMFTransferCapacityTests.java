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
package org.eclipse.sirius.web.restfulemf.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.sirius.web.application.capability.services.api.ICapabilityEvaluator;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.unit.DataSize;

import org.eclipse.sirius.web.restfulemf.application.api.IRestfulEMFReadApplicationService;
import org.eclipse.sirius.web.restfulemf.application.api.IRestfulEMFWriteApplicationService;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceRepresentation;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.controllers.RestfulEMFExceptionHandler;
import org.eclipse.sirius.web.restfulemf.controllers.RestfulEMFResourceController;
import org.eclipse.sirius.web.restfulemf.configuration.RestfulEMFProperties;
import org.eclipse.sirius.web.restfulemf.services.ResourcePaths;

/**
 * Tests the bounded transfer capacity of the REST controller.
 *
 * @author cbrun
 */
public class RestfulEMFTransferCapacityTests {

    private static final String PROJECT_ID = "project";

    private static final String SEPARATOR = "\t";

    private static final String FORMAT = "xmi";

    private static final String GET_METHOD = "GET";

    private static final String DOCUMENT_URI = "/api/rest/projects/project/documents/xmi/document";

    @Test
    public void givenAnActiveTransferWhenAnotherTransferStartsThenServiceUnavailableIsReturned() throws Exception {
        var readApplicationService = mock(IRestfulEMFReadApplicationService.class);
        var capabilityEvaluator = mock(ICapabilityEvaluator.class);
        when(capabilityEvaluator.hasCapability(anyString(), anyString(), anyString())).thenReturn(true);
        var transferStarted = new CountDownLatch(1);
        var releaseTransfer = new CountDownLatch(1);
        when(readApplicationService.getResource(PROJECT_ID, "document", ResourceFormat.XMI, SEPARATOR))
                .thenReturn(new ResourceRepresentation(outputStream -> {
                    transferStarted.countDown();
                    try {
                        releaseTransfer.await();
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                    }
                }, "revision"));
        var controller = new RestfulEMFResourceController(readApplicationService, mock(IRestfulEMFWriteApplicationService.class), capabilityEvaluator,
                new RestfulEMFProperties(false, DataSize.ofMegabytes(1), DataSize.ofMegabytes(1), 1), new ResourcePaths());

        var executor = Executors.newSingleThreadExecutor();
        try {
            Future<?> firstTransfer = executor.submit(() -> {
                try {
                    controller.getResource(PROJECT_ID, FORMAT, SEPARATOR, new MockHttpServletRequest(GET_METHOD, DOCUMENT_URI), new MockHttpServletResponse());
                } catch (IOException exception) {
                    throw new UncheckedIOException(exception);
                }
            });
            assertThat(transferStarted.await(5, TimeUnit.SECONDS)).isTrue();

            assertThatThrownBy(() -> controller.getResource(PROJECT_ID, FORMAT, SEPARATOR, new MockHttpServletRequest(GET_METHOD, DOCUMENT_URI), new MockHttpServletResponse()))
                    .isInstanceOfSatisfying(RestfulEMFException.class, exception -> {
                        assertThat(exception.getError()).isEqualTo(RestfulEMFError.TRANSFER_CAPACITY_EXHAUSTED);
                        var response = new RestfulEMFExceptionHandler().handleRestfulEMFException(exception);
                        assertThat(response.getStatusCode().value()).isEqualTo(503);
                        assertThat(response.getHeaders().getFirst("Retry-After")).isEqualTo("1");
                    });

            releaseTransfer.countDown();
            firstTransfer.get(5, TimeUnit.SECONDS);
            var responseAfterCompletion = new MockHttpServletResponse();
            controller.getResource(PROJECT_ID, FORMAT, SEPARATOR,
                    new MockHttpServletRequest(GET_METHOD, DOCUMENT_URI), responseAfterCompletion);
            assertThat(responseAfterCompletion.getStatus()).isEqualTo(200);
        } finally {
            releaseTransfer.countDown();
            executor.shutdownNow();
        }
    }
}
