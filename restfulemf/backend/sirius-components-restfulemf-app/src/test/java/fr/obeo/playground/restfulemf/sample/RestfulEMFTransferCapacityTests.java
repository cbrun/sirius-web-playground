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
package fr.obeo.playground.restfulemf.sample;

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
import org.springframework.util.unit.DataSize;

import fr.obeo.playground.restfulemf.application.api.IRestfulEMFReadApplicationService;
import fr.obeo.playground.restfulemf.application.api.IRestfulEMFWriteApplicationService;
import fr.obeo.playground.restfulemf.application.api.ResourceFormat;
import fr.obeo.playground.restfulemf.application.api.ResourceRepresentation;
import fr.obeo.playground.restfulemf.application.api.RestfulEMFError;
import fr.obeo.playground.restfulemf.application.api.RestfulEMFException;
import fr.obeo.playground.restfulemf.controllers.RestfulEMFExceptionHandler;
import fr.obeo.playground.restfulemf.controllers.RestfulEMFResourceController;

/**
 * Tests the bounded transfer capacity of the REST controller.
 */
public class RestfulEMFTransferCapacityTests {

    @Test
    public void givenAnActiveTransferWhenAnotherTransferStartsThenServiceUnavailableIsReturned() throws Exception {
        var readApplicationService = mock(IRestfulEMFReadApplicationService.class);
        var capabilityEvaluator = mock(ICapabilityEvaluator.class);
        when(capabilityEvaluator.hasCapability(anyString(), anyString(), anyString())).thenReturn(true);
        var transferStarted = new CountDownLatch(1);
        var releaseTransfer = new CountDownLatch(1);
        when(readApplicationService.getResource("project", "document", ResourceFormat.XMI, "\t"))
                .thenReturn(new ResourceRepresentation(outputStream -> {
                    transferStarted.countDown();
                    try {
                        releaseTransfer.await();
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                    }
                }, "revision"));
        var controller = new RestfulEMFResourceController(readApplicationService, mock(IRestfulEMFWriteApplicationService.class), capabilityEvaluator,
                false, DataSize.ofMegabytes(1), 1);

        var executor = Executors.newSingleThreadExecutor();
        try {
            Future<?> firstTransfer = executor.submit(() -> {
                try {
                    controller.getXMIResource("project", "document", new MockHttpServletResponse());
                } catch (IOException exception) {
                    throw new UncheckedIOException(exception);
                }
            });
            assertThat(transferStarted.await(5, TimeUnit.SECONDS)).isTrue();

            assertThatThrownBy(() -> controller.getXMIResource("project", "document", new MockHttpServletResponse()))
                    .isInstanceOfSatisfying(RestfulEMFException.class,
                            exception -> assertThat(exception.getError()).isEqualTo(RestfulEMFError.TRANSFER_CAPACITY_EXHAUSTED));
            var response = new RestfulEMFExceptionHandler().handleRestfulEMFException(
                    new RestfulEMFException(RestfulEMFError.TRANSFER_CAPACITY_EXHAUSTED, "busy"));
            assertThat(response.getStatusCode().value()).isEqualTo(503);
            assertThat(response.getHeaders().getFirst("Retry-After")).isEqualTo("1");

            releaseTransfer.countDown();
            firstTransfer.get(5, TimeUnit.SECONDS);
        } finally {
            releaseTransfer.countDown();
            executor.shutdownNow();
        }
    }
}
