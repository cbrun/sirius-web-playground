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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.sirius.web.tests.data.GivenSiriusWebServer;
import org.eclipse.sirius.web.tests.services.api.IGivenInitialServerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests of the RESTful EMF read endpoints.
 */
@GivenSiriusWebServer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestfulEMFReadIntegrationTests extends AbstractIntegrationTests {

    private static final String ECORE_PROJECT_ID = "99d336a2-3049-439a-8853-b104ffb22653";

    private static final String ECORE_DOCUMENT_ID = "48dc942a-6b76-4133-bca5-5b29ebee133d";

    private static final String FLOW_PROJECT_ID = "d419bbee-9cba-4b85-972c-660d875ad705";

    @LocalServerPort
    private int port;

    @Autowired
    private IGivenInitialServerState givenInitialServerState;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private WebTestClient webTestClient;

    @Test
    @DisplayName("Given an unchanged document, when read repeatedly, then strong ETags match the actual bytes")
    public void givenUnchangedDocumentWhenReadRepeatedlyThenStrongEtagsMatchTheActualBytes() throws Exception {
        for (String format : List.of("xmi", "bin", "xmi.zip")) {
            String uri = "/api/rest/projects/" + FLOW_PROJECT_ID + "/documents/" + format + "/Flow";
            var first = this.webTestClient.get().uri(uri).exchange().expectStatus().isOk().expectBody(byte[].class).returnResult();
            var second = this.webTestClient.get().uri(uri).exchange().expectStatus().isOk().expectBody(byte[].class).returnResult();
            assertThat(second.getResponseBody()).as(format).isEqualTo(first.getResponseBody());
            String digest = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(first.getResponseBody()));
            assertThat(first.getResponseHeaders().getETag()).as(format).isEqualTo('"' + digest + '"');
            assertThat(second.getResponseHeaders().getETag()).as(format).isEqualTo(first.getResponseHeaders().getETag());
        }
    }

    @BeforeEach
    public void beforeEach() {
        this.givenInitialServerState.initialize();
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port).build();
    }

    @Test
    @DisplayName("Given an Ecore project, when its documents are requested, then their identifiers and names are returned")
    public void givenEcoreProjectWhenItsDocumentsAreRequestedThenTheirIdentifiersAndNamesAreReturned() {
        this.webTestClient.get()
                .uri("/api/rest/projects/{projectId}/documents", ECORE_PROJECT_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(ECORE_DOCUMENT_ID)
                .jsonPath("$[0].name").isEqualTo("Ecore")
                .jsonPath("$[0].path").isEqualTo("Ecore")
                .jsonPath("$[0].readOnly").isEqualTo(false);
    }

    @Test
    @DisplayName("Given an Ecore project, when its EMF formats are requested by name or identifier, then they retain model identifiers")
    public void givenEcoreProjectWhenItsEMFFormatsAreRequestedByNameOrIdentifierThenTheyRetainModelIdentifiers() {
        byte[] xmiByName = this.getBytes("/api/rest/projects/" + ECORE_PROJECT_ID + "/documents/xmi/Ecore");
        byte[] binary = this.getBytes("/api/rest/projects/" + ECORE_PROJECT_ID + "/documents/bin/Ecore");
        byte[] zippedXMI = this.getBytes("/api/rest/projects/" + ECORE_PROJECT_ID + "/documents/xmi.zip/Ecore");

        assertThat(new String(xmiByName, StandardCharsets.UTF_8)).contains("3237b215-ae23-48d7-861e-f542a4b9a4b8");
        assertThat(binary).isNotEmpty();
        assertThat(zippedXMI).startsWith((byte) 'P', (byte) 'K');

        this.givenInitialServerState.initialize();
        this.jdbcTemplate.update("UPDATE document SET name = ? WHERE id = ?::uuid", "../Ecore", ECORE_DOCUMENT_ID);
        byte[] xmiById = this.getBytes("/api/rest/projects/" + ECORE_PROJECT_ID + "/documents/xmi/_by-id/" + ECORE_DOCUMENT_ID);
        assertThat(new String(xmiById, StandardCharsets.UTF_8)).contains("3237b215-ae23-48d7-861e-f542a4b9a4b8");
    }

    @Test
    @DisplayName("Given Ecore and Flow support, when registered packages are requested, then both metamodels are returned")
    public void givenEcoreAndFlowSupportWhenRegisteredPackagesAreRequestedThenBothMetamodelsAreReturned() throws Exception {
        byte[] content = this.getBytes("/api/rest/projects/" + FLOW_PROJECT_ID + "/epackages/bin");
        XMLResource resource = new XMLResourceImpl();
        Map<String, Object> options = new HashMap<>();
        options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);
        resource.load(new ByteArrayInputStream(content), options);

        assertThat(resource.getContents())
                .filteredOn(EPackage.class::isInstance)
                .map(EPackage.class::cast)
                .extracting(EPackage::getNsURI)
                .contains("http://www.eclipse.org/emf/2002/Ecore", "http://www.obeo.fr/dsl/designer/sample/flow");
    }

    @Test
    @DisplayName("Given a Flow project, when XMI and CSV are requested, then Flow model data is returned")
    public void givenFlowProjectWhenXMIAndCSVAreRequestedThenFlowModelDataIsReturned() {
        byte[] xmi = this.getBytes("/api/rest/projects/" + FLOW_PROJECT_ID + "/documents/xmi/Flow");
        String defaultCSV = this.getString("/api/rest/projects/" + FLOW_PROJECT_ID + "/documents/csv/Flow");
        String customCSV = this.getString("/api/rest/projects/" + FLOW_PROJECT_ID + "/documents/csv/Flow?sep=;");

        assertThat(new String(xmi, StandardCharsets.UTF_8)).contains("CompositeProcessor1", "http://www.obeo.fr/dsl/designer/sample/flow");
        assertThat(defaultCSV).contains("\teClass\t", "CompositeProcessor");
        assertThat(customCSV).contains(";eClass;", "CompositeProcessor");
    }

    @Test
    @DisplayName("Given unknown project or document identifiers, when a resource is requested, then not found is returned")
    public void givenUnknownProjectOrDocumentIdentifiersWhenAResourceIsRequestedThenNotFoundIsReturned() {
        this.webTestClient.get().uri("/api/rest/projects/unknown/documents").exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("NOT_FOUND");
        this.webTestClient.get().uri("/api/rest/projects/{projectId}/documents/xmi/unknown", ECORE_PROJECT_ID).exchange().expectStatus().isNotFound();
    }

    private byte[] getBytes(String uri) {
        return this.webTestClient.get().uri(uri).exchange().expectStatus().isOk().expectBody(byte[].class).returnResult().getResponseBody();
    }

    private String getString(String uri) {
        return this.webTestClient.get().uri(uri).exchange().expectStatus().isEqualTo(HttpStatus.OK).expectBody(String.class).returnResult().getResponseBody();
    }
}
