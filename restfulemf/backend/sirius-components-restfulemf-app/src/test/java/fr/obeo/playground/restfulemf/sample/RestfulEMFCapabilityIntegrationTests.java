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

import org.eclipse.sirius.web.application.capability.SiriusWebCapabilities;
import org.eclipse.sirius.web.application.capability.services.CapabilityVote;
import org.eclipse.sirius.web.application.capability.services.api.ICapabilityVoter;
import org.eclipse.sirius.web.tests.data.GivenSiriusWebServer;
import org.eclipse.sirius.web.tests.services.api.IGivenInitialServerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests of the RESTful EMF capability checks.
 */
@GivenSiriusWebServer
@Import(RestfulEMFCapabilityIntegrationTests.DeniedCapabilitiesConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestfulEMFCapabilityIntegrationTests extends AbstractIntegrationTests {

    private static final String FLOW_PROJECT_ID = "d419bbee-9cba-4b85-972c-660d875ad705";

    private static final String FLOW_XMI_URI = "/api/rest/projects/" + FLOW_PROJECT_ID + "/Flow/xmi";

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
    @DisplayName("Given denied project capabilities, when a document is read or written, then forbidden is returned")
    public void givenDeniedProjectCapabilitiesWhenADocumentIsReadOrWrittenThenForbiddenIsReturned() {
        this.webTestClient.get().uri("/api/rest/projects/{projectId}/documents", FLOW_PROJECT_ID).exchange().expectStatus().isForbidden();
        this.webTestClient.get().uri("/api/rest/projects/{projectId}/epackages/bin", FLOW_PROJECT_ID).exchange().expectStatus().isForbidden();
        this.webTestClient.get().uri(FLOW_XMI_URI).exchange().expectStatus().isForbidden();
        this.webTestClient.put().uri(FLOW_XMI_URI)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(new byte[] { 1 })
                .exchange()
                .expectStatus().isForbidden();
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class DeniedCapabilitiesConfiguration {

        @Bean
        ICapabilityVoter deniedFlowProjectCapabilities() {
            return (type, identifier, capability) -> {
                if (SiriusWebCapabilities.PROJECT.equals(type) && FLOW_PROJECT_ID.equals(identifier)
                        && (SiriusWebCapabilities.Project.VIEW.equals(capability) || SiriusWebCapabilities.Project.EDIT.equals(capability))) {
                    return CapabilityVote.DENIED;
                }
                return CapabilityVote.ABSTAIN;
            };
        }
    }
}
