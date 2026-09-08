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

import org.eclipse.sirius.web.tests.data.GivenSiriusWebServer;
import org.eclipse.sirius.web.tests.services.api.IGivenInitialServerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests of mandatory optimistic concurrency.
 *
 * @author cbrun
 */
@GivenSiriusWebServer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "sirius.web.restful-emf.require-if-match=true")
public class RestfulEMFStrictConcurrencyIntegrationTests extends AbstractIntegrationTests {

    private static final String FLOW_XMI_URI = "/api/rest/projects/d419bbee-9cba-4b85-972c-660d875ad705/documents/xmi/Flow";

    @LocalServerPort
    private int port;

    @Autowired
    private IGivenInitialServerState givenInitialServerState;

    private WebTestClient webTestClient;

    @BeforeEach
    public void beforeEach() {
        this.givenInitialServerState.initialize();
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port).build();
    }

    @Test
    @DisplayName("Given strict concurrency, when a write omits If-Match, then precondition required is returned")
    public void givenStrictConcurrencyWhenAWriteOmitsIfMatchThenPreconditionRequiredIsReturned() {
        byte[] xmi = this.webTestClient.get().uri(FLOW_XMI_URI).exchange().expectStatus().isOk().expectBody(byte[].class).returnResult().getResponseBody();

        this.webTestClient.put().uri(FLOW_XMI_URI)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(xmi)
                .exchange()
                .expectStatus().isEqualTo(428);
    }
}
