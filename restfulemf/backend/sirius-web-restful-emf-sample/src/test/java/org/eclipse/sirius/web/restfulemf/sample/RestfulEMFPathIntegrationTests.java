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

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.sirius.web.tests.data.GivenSiriusWebServer;
import org.eclipse.sirius.web.tests.services.api.IGivenInitialServerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Imports a directory through the standard EMF runtime, without the optional REST client.
 *
 * @author cbrun
 */
@GivenSiriusWebServer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestfulEMFPathIntegrationTests extends AbstractIntegrationTests {

    private static final String PROJECT_ID = "99d336a2-3049-439a-8853-b104ffb22653";

    private static final String DOCUMENTS = "/api/rest/projects/" + PROJECT_ID + "/documents";

    @LocalServerPort
    private int port;

    @Autowired
    private IGivenInitialServerState givenInitialServerState;

    @TempDir
    private Path directory;

    private WebTestClient client;

    @BeforeEach
    public void beforeEach() {
        this.givenInitialServerState.initialize();
        this.client = WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port).build();
    }

    @Test
    @DisplayName("Given local cyclic models, when saved in either order with native EMF, then paths and references survive reload")
    public void givenLocalCyclicModelsWhenSavedInEitherOrderWithNativeEMFThenPathsAndReferencesSurviveReload() throws IOException {
        for (boolean reverse : List.of(false, true)) {
            var resources = this.resourceSet();
            URI local = URI.createFileURI(this.directory.toAbsolutePath().toString()).appendSegment("");
            Resource first = resources.createResource(local.trimSegments(1).appendSegments(new String[] { "domain", "first.ecore" }));
            Resource second = resources.createResource(local.trimSegments(1).appendSegments(new String[] { "common", "second.ecore" }));
            EClass firstClass = this.addPackage(first, "first");
            EClass secondClass = this.addPackage(second, "second");
            this.addReference(firstClass, secondClass);
            this.addReference(secondClass, firstClass);
            first.save(Map.of());
            second.save(Map.of());

            String prefix = "upload-" + UUID.randomUUID();
            URI remote = URI.createURI("http://localhost:" + this.port + DOCUMENTS + "/xmi/" + prefix + "/");
            resources.getURIConverter().getURIMap().put(local, remote);
            List<Resource> ordered = List.of(first, second);
            if (reverse) {
                ordered = List.of(second, first);
            }
            ordered.getFirst().save(Map.of());
            this.givenInitialServerState.initialize();
            ordered.getLast().save(Map.of());
            first.save(Map.of());

            var downloaded = this.resourceSet();
            Resource downloadedFirst = downloaded.getResource(remote.trimSegments(1).appendSegments(new String[] { "domain", "first.ecore" }), true);
            EClass loadedFirst = (EClass) ((EPackage) downloadedFirst.getContents().getFirst()).getEClassifiers().getFirst();
            EClass loadedSecond = (EClass) loadedFirst.getEReferences().getFirst().getEType();
            assertThat(loadedSecond.eIsProxy()).isFalse();
            assertThat(loadedSecond.getName()).isEqualTo("second");
            assertThat(loadedSecond.getEReferences().getFirst().getEType()).isSameAs(loadedFirst);
            this.client.get().uri(DOCUMENTS).exchange().expectStatus().isOk().expectBody()
                    .jsonPath("$[?(@.path == '" + prefix + "/domain/first.ecore')]").value(values -> assertThat((List<?>) values).hasSize(1));
        }
    }

    @Test
    @DisplayName("Given a new document path, when two create-only PUTs race, then exactly one creates it")
    public void givenNewDocumentPathWhenTwoCreateOnlyPutsRaceThenExactlyOneCreatesIt() {
        String path = DOCUMENTS + "/xmi/race-" + UUID.randomUUID() + ".ecore";
        byte[] content = this.client.get().uri(DOCUMENTS + "/xmi/Ecore").exchange().expectStatus().isOk().expectBody().returnResult().getResponseBody();
        var first = CompletableFuture.supplyAsync(() -> this.create(path, content));
        var second = CompletableFuture.supplyAsync(() -> this.create(path, content));
        assertThat(List.of(first.join(), second.join())).containsExactlyInAnyOrder(201, 412);
        this.client.head().uri(path).exchange().expectStatus().isOk().expectHeader().exists(HttpHeaders.ETAG).expectBody().isEmpty();
    }

    @Test
    @DisplayName("Given a Unicode path, when PUT creates then replaces it, then Location and conditional semantics are coherent")
    public void givenUnicodePathWhenPutCreatesThenReplacesItThenLocationAndConditionalSemanticsAreCoherent() {
        String path = DOCUMENTS + "/xmi/école/model space.ecore";
        byte[] content = this.client.get().uri(DOCUMENTS + "/xmi/Ecore").exchange().expectStatus().isOk().expectBody().returnResult().getResponseBody();
        this.client.put().uri(path).bodyValue(content).exchange().expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION).expectHeader().doesNotExist(HttpHeaders.ETAG);
        String revision = this.client.head().uri(path).exchange().expectStatus().isOk().expectBody().returnResult().getResponseHeaders().getETag();
        this.client.put().uri(path).header(HttpHeaders.IF_MATCH, revision).bodyValue(content).exchange().expectStatus().isNoContent()
                .expectHeader().doesNotExist(HttpHeaders.ETAG);
        assertThat(this.create(path, content)).isEqualTo(412);
        this.client.put().uri(DOCUMENTS + "/xmi/_by-id/" + UUID.randomUUID()).bodyValue(content).exchange().expectStatus().isNotFound();
    }

    private int create(String path, byte[] content) {
        return this.client.put().uri(path).header(HttpHeaders.IF_NONE_MATCH, "*").bodyValue(content).exchange().expectBody().returnResult().getStatus().value();
    }

    private ResourceSetImpl resourceSet() {
        var resources = new ResourceSetImpl();
        resources.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        resources.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
        return resources;
    }

    private EClass addPackage(Resource resource, String name) {
        var ePackage = EcoreFactory.eINSTANCE.createEPackage();
        ePackage.setName(name);
        ePackage.setNsPrefix(name);
        ePackage.setNsURI("urn:test:" + name);
        var eClass = EcoreFactory.eINSTANCE.createEClass();
        eClass.setName(name);
        ePackage.getEClassifiers().add(eClass);
        resource.getContents().add(ePackage);
        return eClass;
    }

    private void addReference(EClass owner, EClass target) {
        var reference = EcoreFactory.eINSTANCE.createEReference();
        reference.setName("other");
        reference.setEType(target);
        owner.getEStructuralFeatures().add(reference);
    }
}
