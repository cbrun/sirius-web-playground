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
package org.eclipse.sirius.web.restfulemf.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.net.httpserver.HttpServer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.junit.jupiter.api.Test;

/**
 * Client integration tests against a real HTTP transport and binary EMF payloads.
 *
 * @author cbrun
 */
public class RestfulEMFClientTests {

    private static final String DYNAMIC_PACKAGE_NAME = "dynamic";

    private static final String TARGET_REFERENCE_NAME = "target";

    private static final String NESTED_PACKAGE_NAME = "nested";

    private static final String NESTED_PACKAGE_NS_URI = "urn:nested";

    private static final String FIRST_RESOURCE_URI = "http://example.org/documents/bin/domain/a.ecore";

    private static final String SECOND_RESOURCE_URI = "http://example.org/documents/bin/common/b.ecore";

    private static final String HOLDER_ID = "holder-id";

    private static final String DOCUMENTS_PATH = "documents";

    private static final String METAMODELS_PATH = "epackages/bin";

    private static final String FIRST_DOCUMENT_PATH = "documents/bin/domain/a.ecore";

    private static final String SECOND_DOCUMENT_PATH = "documents/bin/common/b.ecore";

    private static final String HOST = "127.0.0.1";

    private static final String CONTEXT_PROJECT_ENDPOINT = "/context/api/rest/projects/project/";

    private static final String BEARER_TOKEN = "Bearer test";

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String ETAG_HEADER = "ETag";

    private static final String INITIAL_ETAG = "\"revision-1\"";

    private static final String ORIGIN_PREFIX = "http://127.0.0.1:";

    private static final String METADATA_URI = "sirius:///metadata";

    private static final String PROJECT_ENDPOINT = "/api/rest/projects/p/";

    private static final String PROJECT_PATH = "/projects/p";

    private static final String VALID_DOCUMENT_LISTING = "[{\"id\":\"a\",\"name\":\"a\",\"path\":\"a\",\"readOnly\":false}]";

    private static final String PRESERVED_PACKAGE_KEY = "preserved";

    @Test
    public void loadsCompleteProjectAndPreservesGeneratedTypesReferencesAndIds() throws Exception {
        EPackage generatedBeforeLoad = EcoreUtil.copy(EcorePackage.eINSTANCE);
        var factory = EcoreFactory.eINSTANCE;
        EPackage dynamic = factory.createEPackage();
        dynamic.setName(DYNAMIC_PACKAGE_NAME);
        dynamic.setNsPrefix(DYNAMIC_PACKAGE_NAME);
        dynamic.setNsURI("urn:dynamic");
        EClass holder = factory.createEClass();
        holder.setName("Holder");
        dynamic.getEClassifiers().add(holder);
        var reference = factory.createEReference();
        reference.setName(TARGET_REFERENCE_NAME);
        reference.setEType(EcorePackage.Literals.ECLASS);
        holder.getEStructuralFeatures().add(reference);
        EPackage nested = factory.createEPackage();
        nested.setName(NESTED_PACKAGE_NAME);
        nested.setNsPrefix(NESTED_PACKAGE_NAME);
        nested.setNsURI(NESTED_PACKAGE_NS_URI);
        dynamic.getESubpackages().add(nested);

        var metadata = new XMLResourceImpl(URI.createURI("sirius:///project/epackages"));
        var copier = new EcoreUtil.Copier();
        metadata.getContents().addAll(copier.copyAll(List.of(dynamic, EcorePackage.eINSTANCE)));
        copier.copyReferences();
        var first = new XMLResourceImpl(URI.createURI(FIRST_RESOURCE_URI));
        var second = new XMLResourceImpl(URI.createURI(SECOND_RESOURCE_URI));
        var object = dynamic.getEFactoryInstance().create(holder);
        var target = factory.createEClass();
        target.setName("Target");
        object.eSet(reference, target);
        first.getContents().add(object);
        first.setID(object, HOLDER_ID);
        second.getContents().add(target);
        second.setID(target, "target-id");

        var payloads = Map.of(DOCUMENTS_PATH, "[{\"path\":\"domain/a.ecore\"},{\"path\":\"common/b.ecore\"}]".getBytes(StandardCharsets.UTF_8),
                METAMODELS_PATH, this.binary(metadata), FIRST_DOCUMENT_PATH, this.binary(first), SECOND_DOCUMENT_PATH, this.binary(second));
        var requests = new ArrayList<String>();
        var saved = new AtomicReference<byte[]>();
        var match = new AtomicReference<String>();
        HttpServer server = HttpServer.create(new InetSocketAddress(HOST, 0), 0);
        server.createContext(CONTEXT_PROJECT_ENDPOINT, exchange -> {
            String path = exchange.getRequestURI().getPath().substring(CONTEXT_PROJECT_ENDPOINT.length());
            requests.add(path);
            if (!BEARER_TOKEN.equals(exchange.getRequestHeaders().getFirst(AUTHORIZATION_HEADER))) {
                exchange.sendResponseHeaders(401, -1);
            } else if ("PUT".equals(exchange.getRequestMethod())) {
                match.set(exchange.getRequestHeaders().getFirst("If-Match"));
                saved.set(exchange.getRequestBody().readAllBytes());
                exchange.getResponseHeaders().set(ETAG_HEADER, "\"revision-2\"");
                exchange.sendResponseHeaders(204, -1);
            } else {
                byte[] bytes = payloads.get(path);
                exchange.getResponseHeaders().set(ETAG_HEADER, INITIAL_ETAG);
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
            exchange.close();
        });
        server.start();
        try {
            var resourceSet = new ResourceSetImpl();
            resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
            new RestfulEMFClient(Map.of(AUTHORIZATION_HEADER, BEARER_TOKEN)).loadProject(
                    URI.createURI(ORIGIN_PREFIX + server.getAddress().getPort() + "/context/projects/project/edit/representation?view=tree#selection"), resourceSet);
            assertThat(requests).containsExactly(DOCUMENTS_PATH, METAMODELS_PATH, SECOND_DOCUMENT_PATH, FIRST_DOCUMENT_PATH);
            String endpoint = ORIGIN_PREFIX + server.getAddress().getPort() + CONTEXT_PROJECT_ENDPOINT;
            assertThat(resourceSet.getResources()).extracting(resource -> resource.getURI().toString())
                    .containsExactly(endpoint + SECOND_DOCUMENT_PATH, endpoint + FIRST_DOCUMENT_PATH);
            assertThat(resourceSet.getPackageRegistry().getEPackage(EcorePackage.eNS_URI)).isSameAs(EcorePackage.eINSTANCE);
            assertThat(EcoreUtil.equals(generatedBeforeLoad, EcorePackage.eINSTANCE)).isTrue();
            assertThat(resourceSet.getPackageRegistry().getEPackage(NESTED_PACKAGE_NS_URI)).isNotNull();
            var document = resourceSet.getResources().get(1);
            var loaded = document.getContents().getFirst();
            var loadedReference = loaded.eClass().getEStructuralFeature(TARGET_REFERENCE_NAME);
            assertThat(loadedReference.getEType()).isSameAs(EcorePackage.Literals.ECLASS);
            assertThat(loaded.eGet(loadedReference)).isSameAs(resourceSet.getResources().getFirst().getContents().getFirst()).isInstanceOf(EClass.class);
            assertThat(((XMLResource) document).getID(loaded)).isEqualTo(HOLDER_ID);
            document.save(Map.of());
            assertThat(match.get()).isEqualTo(INITIAL_ETAG);
            this.assertSavedDocument(saved.get(), resourceSet.getPackageRegistry());
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void supportsProjectAndRestUrlsAndRejectsInvalidUrls() throws Exception {
        byte[] metadata = this.binary(new XMLResourceImpl(URI.createURI(METADATA_URI)));
        HttpServer server = HttpServer.create(new InetSocketAddress(HOST, 0), 0);
        server.createContext(PROJECT_ENDPOINT, exchange -> {
            byte[] response = metadata;
            if (exchange.getRequestURI().getPath().endsWith(DOCUMENTS_PATH)) {
                response = "[]".getBytes(StandardCharsets.UTF_8);
            }
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        try {
            String origin = ORIGIN_PREFIX + server.getAddress().getPort();
            for (String suffix : List.of(PROJECT_PATH, "/projects/p/", "/api/rest/projects/p/doc/bin", "/projects/p/edit/x")) {
                assertThat(new RestfulEMFClient().loadProject(URI.createURI(origin + suffix)).getResources()).isEmpty();
            }
            for (String invalid : List.of("file:///projects/p", origin, origin + "/projects/", origin + "/projects/%2E%2E", origin + "/projects/a%2Fb")) {
                assertThatThrownBy(() -> new RestfulEMFClient().loadProject(URI.createURI(invalid))).isInstanceOf(IllegalArgumentException.class);
            }
            var populated = new ResourceSetImpl();
            populated.getResources().add(new XMLResourceImpl());
            assertThatThrownBy(() -> new RestfulEMFClient().loadProject(URI.createURI(origin + PROJECT_PATH), populated)).isInstanceOf(IllegalArgumentException.class);
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void cleansUpAfterMalformedListingAndFailedDocumentLoad() throws Exception {
        byte[] metadata = this.binary(new XMLResourceImpl(URI.createURI(METADATA_URI)));
        var listing = new AtomicReference<>(VALID_DOCUMENT_LISTING);
        HttpServer server = HttpServer.create(new InetSocketAddress(HOST, 0), 0);
        server.createContext(PROJECT_ENDPOINT, exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.endsWith("/documents/bin/a")) {
                exchange.sendResponseHeaders(500, -1);
            } else {
                byte[] response = metadata;
                if (path.endsWith(DOCUMENTS_PATH)) {
                    response = listing.get().getBytes(StandardCharsets.UTF_8);
                }
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
            }
            exchange.close();
        });
        server.start();
        try {
            var resourceSet = new ResourceSetImpl();
            var handlers = List.copyOf(resourceSet.getURIConverter().getURIHandlers());
            resourceSet.getPackageRegistry().put(PRESERVED_PACKAGE_KEY, EcorePackage.eINSTANCE);
            URI logical = URI.createURI("test:/logical");
            URI physical = URI.createURI("test:/physical");
            resourceSet.getURIConverter().getURIMap().put(logical, physical);
            for (String documentListing : List.of(VALID_DOCUMENT_LISTING, "{}", "[{\"path\":42}]", "[{\"path\":\"a\"},{\"path\":\"a\"}]",
                    "[{\"path\":\"../a\"}]", "[{\"path\":\"a//b\"}]", "[{\"path\":\"a%2Fb\"}]")) {
                listing.set(documentListing);
                assertThatThrownBy(() -> new RestfulEMFClient().loadProject(URI.createURI(ORIGIN_PREFIX + server.getAddress().getPort() + PROJECT_PATH), resourceSet))
                        .isInstanceOf(IOException.class);
                assertThat(resourceSet.getResources()).isEmpty();
                assertThat(resourceSet.getPackageRegistry()).containsEntry(PRESERVED_PACKAGE_KEY, EcorePackage.eINSTANCE);
                assertThat(resourceSet.getURIConverter().getURIMap()).containsEntry(logical, physical).hasSize(1);
                assertThat(resourceSet.getURIConverter().getURIHandlers()).containsExactlyElementsOf(handlers);
            }
        } finally {
            server.stop(0);
        }
    }

    private void assertSavedDocument(byte[] content, EPackage.Registry packages) throws IOException {
        var roundTrip = new XMLResourceImpl(URI.createURI(FIRST_RESOURCE_URI));
        var roundTripSet = new ResourceSetImpl();
        roundTripSet.getPackageRegistry().putAll(packages);
        roundTripSet.getResources().add(roundTrip);
        roundTrip.load(new ByteArrayInputStream(content), Map.of(XMLResource.OPTION_BINARY, true));
        var loaded = roundTrip.getContents().getFirst();
        assertThat(roundTrip.getID(loaded)).isEqualTo(HOLDER_ID);
        var reference = loaded.eClass().getEStructuralFeature(TARGET_REFERENCE_NAME);
        assertThat(EcoreUtil.getURI((EObject) loaded.eGet(reference, false)).toString()).isEqualTo(SECOND_RESOURCE_URI + "#target-id");
    }

    private byte[] binary(XMLResource resource) throws IOException {
        var output = new ByteArrayOutputStream();
        resource.save(output, Map.of(XMLResource.OPTION_BINARY, true));
        return output.toByteArray();
    }
}
