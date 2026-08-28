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

import java.nio.charset.StandardCharsets;

import org.eclipse.sirius.web.tests.data.GivenSiriusWebServer;
import org.eclipse.sirius.web.tests.services.api.IGivenInitialServerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests of the RESTful EMF write endpoints.
 */
@GivenSiriusWebServer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestfulEMFWriteIntegrationTests extends AbstractIntegrationTests {

    private static final String FLOW_PROJECT_ID = "d419bbee-9cba-4b85-972c-660d875ad705";

    private static final String FLOW_DOCUMENT_ID = "96d6cae1-da84-40d8-94c3-f4af14462485";

    private static final String FLOW_XMI_URI = "/api/rest/projects/" + FLOW_PROJECT_ID + "/Flow/xmi";

    @LocalServerPort
    private int port;

    @Autowired
    private IGivenInitialServerState givenInitialServerState;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private WebTestClient webTestClient;

    @BeforeEach
    public void beforeEach() {
        this.givenInitialServerState.initialize();
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port).build();
    }

    @Test
    @DisplayName("Given a Flow document, when updated as XMI, then the change is persisted with its identifiers")
    public void givenFlowDocumentWhenUpdatedAsXMIThenTheChangeIsPersistedWithItsIdentifiers() {
        byte[] initialXMI = this.getBytes(FLOW_XMI_URI);
        String updatedXMI = new String(initialXMI, StandardCharsets.UTF_8).replace("CompositeProcessor1", "CompositeProcessorRenamed");

        this.put(FLOW_XMI_URI, updatedXMI.getBytes(StandardCharsets.UTF_8)).expectStatus().isOk();
        assertThat(new String(this.getBytes(FLOW_XMI_URI), StandardCharsets.UTF_8))
                .contains("CompositeProcessorRenamed", "aac30ae6-05b1-4e98-9e97-e3b440d43e17");

        this.givenInitialServerState.initialize();
        assertThat(new String(this.getBytes(FLOW_XMI_URI), StandardCharsets.UTF_8)).contains("CompositeProcessorRenamed");
    }

    @Test
    @DisplayName("Given a Flow document, when its zipped XMI and binary representations are written back, then both are accepted")
    public void givenFlowDocumentWhenItsZippedXMIAndBinaryRepresentationsAreWrittenBackThenBothAreAccepted() {
        String zippedURI = "/api/rest/projects/" + FLOW_PROJECT_ID + "/Flow/xmi.zip";
        String binaryURI = "/api/rest/projects/" + FLOW_PROJECT_ID + "/Flow/bin";

        this.put(zippedURI, this.getBytes(zippedURI)).expectStatus().isOk();
        this.put(binaryURI, this.getBytes(binaryURI)).expectStatus().isOk();

        assertThat(this.getBytes(zippedURI)).isNotEmpty();
        assertThat(this.getBytes(binaryURI)).isNotEmpty();
    }

    @Test
    @DisplayName("Given invalid EMF input, when it is uploaded, then bad request is returned")
    public void givenInvalidEMFInputWhenItIsUploadedThenBadRequestIsReturned() {
        byte[] unsafeXML = ("<?xml version=\"1.0\"?><!DOCTYPE xmi:XMI [<!ENTITY xxe SYSTEM \"file:///does-not-exist\">]>"
                + "<xmi:XMI xmlns:xmi=\"http://www.omg.org/XMI\">&xxe;</xmi:XMI>").getBytes(StandardCharsets.UTF_8);

        this.put(FLOW_XMI_URI, unsafeXML).expectStatus().isBadRequest();
        this.put("/api/rest/projects/" + FLOW_PROJECT_ID + "/Flow/bin", new byte[] { 1, 2, 3 }).expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Given a read-only Flow document, when an update is requested, then forbidden is returned")
    public void givenReadOnlyFlowDocumentWhenAnUpdateIsRequestedThenForbiddenIsReturned() {
        byte[] xmi = this.getBytes(FLOW_XMI_URI);
        this.givenInitialServerState.initialize();
        this.jdbcTemplate.update("UPDATE document SET is_read_only = true WHERE id = ?::uuid", FLOW_DOCUMENT_ID);

        this.put(FLOW_XMI_URI, xmi).expectStatus().isForbidden();
    }

    @Test
    @DisplayName("Given CSV output, when a CSV update is requested, then method not allowed is returned")
    public void givenCSVOutputWhenACSVUpdateIsRequestedThenMethodNotAllowedIsReturned() {
        this.put("/api/rest/projects/" + FLOW_PROJECT_ID + "/Flow/csv", "id".getBytes(StandardCharsets.UTF_8))
                .expectStatus().isEqualTo(405);
    }

    private byte[] getBytes(String uri) {
        return this.webTestClient.get().uri(uri).exchange().expectStatus().isOk().expectBody(byte[].class).returnResult().getResponseBody();
    }

    private WebTestClient.ResponseSpec put(String uri, byte[] content) {
        return this.webTestClient.put()
                .uri(uri)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(content)
                .exchange();
    }
}
