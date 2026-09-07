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
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class RestfulEMFClientTests {

    @Test
    public void loadsCompleteProjectAndPreservesGeneratedTypesReferencesAndIds() throws Exception {
        EPackage generatedBeforeLoad = EcoreUtil.copy(EcorePackage.eINSTANCE);
        var factory = EcoreFactory.eINSTANCE;
        EPackage dynamic = factory.createEPackage();
        dynamic.setName("dynamic");
        dynamic.setNsPrefix("dynamic");
        dynamic.setNsURI("urn:dynamic");
        EClass holder = factory.createEClass();
        holder.setName("Holder");
        dynamic.getEClassifiers().add(holder);
        var reference = factory.createEReference();
        reference.setName("target");
        reference.setEType(EcorePackage.Literals.ECLASS);
        holder.getEStructuralFeatures().add(reference);
        EPackage nested = factory.createEPackage();
        nested.setName("nested");
        nested.setNsPrefix("nested");
        nested.setNsURI("urn:nested");
        dynamic.getESubpackages().add(nested);

        var metadata = new XMLResourceImpl(URI.createURI("sirius:///project/epackages"));
        var copier = new EcoreUtil.Copier();
        metadata.getContents().addAll(copier.copyAll(List.of(dynamic, EcorePackage.eINSTANCE)));
        copier.copyReferences();
        var first = new XMLResourceImpl(URI.createURI("sirius:///a"));
        var second = new XMLResourceImpl(URI.createURI("sirius:///b"));
        var object = dynamic.getEFactoryInstance().create(holder);
        var target = factory.createEClass();
        target.setName("Target");
        object.eSet(reference, target);
        first.getContents().add(object);
        first.setID(object, "holder-id");
        second.getContents().add(target);
        second.setID(target, "target-id");

        var payloads = Map.of("documents", "{\"b\":\"same name\",\"a\":\"same name\"}".getBytes(StandardCharsets.UTF_8),
                "epackages/bin", this.binary(metadata), "a/bin", this.binary(first), "b/bin", this.binary(second));
        var requests = new ArrayList<String>();
        var saved = new AtomicReference<byte[]>();
        var match = new AtomicReference<String>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/context/api/rest/projects/project/", exchange -> {
            String path = exchange.getRequestURI().getPath().substring("/context/api/rest/projects/project/".length());
            requests.add(path);
            if (!"Bearer test".equals(exchange.getRequestHeaders().getFirst("Authorization"))) {
                exchange.sendResponseHeaders(401, -1);
            } else if ("PUT".equals(exchange.getRequestMethod())) {
                match.set(exchange.getRequestHeaders().getFirst("If-Match"));
                saved.set(exchange.getRequestBody().readAllBytes());
                exchange.getResponseHeaders().set("ETag", "\"revision-2\"");
                exchange.sendResponseHeaders(204, -1);
            } else {
                byte[] bytes = payloads.get(path);
                exchange.getResponseHeaders().set("ETag", "\"revision-1\"");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
            exchange.close();
        });
        server.start();
        try {
            var resourceSet = new ResourceSetImpl();
            resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
            new RestfulEMFClient(Map.of("Authorization", "Bearer test")).loadProject(
                    URI.createURI("http://127.0.0.1:" + server.getAddress().getPort() + "/context/projects/project/edit/representation?view=tree#selection"), resourceSet);
            assertThat(requests).containsExactly("documents", "epackages/bin", "a/bin", "b/bin");
            assertThat(resourceSet.getResources()).extracting(resource -> resource.getURI().toString()).containsExactly("sirius:///a", "sirius:///b");
            assertThat(resourceSet.getPackageRegistry().getEPackage(EcorePackage.eNS_URI)).isSameAs(EcorePackage.eINSTANCE);
            assertThat(EcoreUtil.equals(generatedBeforeLoad, EcorePackage.eINSTANCE)).isTrue();
            assertThat(resourceSet.getPackageRegistry().getEPackage("urn:nested")).isNotNull();
            var loaded = resourceSet.getResources().getFirst().getContents().getFirst();
            var loadedReference = loaded.eClass().getEStructuralFeature("target");
            assertThat(loadedReference.getEType()).isSameAs(EcorePackage.Literals.ECLASS);
            assertThat(loaded.eGet(loadedReference)).isSameAs(resourceSet.getResources().get(1).getContents().getFirst()).isInstanceOf(EClass.class);
            assertThat(((XMLResource) resourceSet.getResources().getFirst()).getID(loaded)).isEqualTo("holder-id");
            resourceSet.getResources().getFirst().save(Map.of());
            assertThat(match.get()).isEqualTo("\"revision-1\"");
            var roundTrip = new XMLResourceImpl(URI.createURI("sirius:///a"));
            var roundTripSet = new ResourceSetImpl();
            roundTripSet.getPackageRegistry().putAll(resourceSet.getPackageRegistry());
            roundTripSet.getResources().add(roundTrip);
            roundTrip.load(new ByteArrayInputStream(saved.get()), Map.of(XMLResource.OPTION_BINARY, true));
            assertThat(roundTrip.getID(roundTrip.getContents().getFirst())).isEqualTo("holder-id");
            assertThat(EcoreUtil.getURI((org.eclipse.emf.ecore.EObject) roundTrip.getContents().getFirst().eGet(loadedReference, false)).toString())
                    .isEqualTo("sirius:///b#target-id");
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void supportsProjectAndRestUrlsAndRejectsInvalidUrls() throws Exception {
        byte[] metadata = this.binary(new XMLResourceImpl(URI.createURI("sirius:///metadata")));
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/rest/projects/p/", exchange -> {
            byte[] response = exchange.getRequestURI().getPath().endsWith("documents") ? "{}".getBytes(StandardCharsets.UTF_8) : metadata;
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        try {
            String origin = "http://127.0.0.1:" + server.getAddress().getPort();
            for (String suffix : List.of("/projects/p", "/projects/p/", "/api/rest/projects/p/doc/bin", "/projects/p/edit/x")) {
                assertThat(new RestfulEMFClient().loadProject(URI.createURI(origin + suffix)).getResources()).isEmpty();
            }
            for (String invalid : List.of("file:///projects/p", origin, origin + "/projects/", origin + "/projects/%2E%2E", origin + "/projects/a%2Fb")) {
                assertThatThrownBy(() -> new RestfulEMFClient().loadProject(URI.createURI(invalid))).isInstanceOf(IllegalArgumentException.class);
            }
            var populated = new ResourceSetImpl();
            populated.getResources().add(new XMLResourceImpl());
            assertThatThrownBy(() -> new RestfulEMFClient().loadProject(URI.createURI(origin + "/projects/p"), populated)).isInstanceOf(IllegalArgumentException.class);
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void cleansUpAfterMalformedListingAndFailedDocumentLoad() throws Exception {
        byte[] metadata = this.binary(new XMLResourceImpl(URI.createURI("sirius:///metadata")));
        var listing = new AtomicReference<>("{\"a\":\"a\"}");
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/rest/projects/p/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.endsWith("/a/bin")) {
                exchange.sendResponseHeaders(500, -1);
            } else {
                byte[] response = path.endsWith("documents") ? listing.get().getBytes(StandardCharsets.UTF_8) : metadata;
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
            }
            exchange.close();
        });
        server.start();
        try {
            var resourceSet = new ResourceSetImpl();
            var handlers = List.copyOf(resourceSet.getURIConverter().getURIHandlers());
            resourceSet.getPackageRegistry().put("preserved", EcorePackage.eINSTANCE);
            URI logical = URI.createURI("test:/logical");
            URI physical = URI.createURI("test:/physical");
            resourceSet.getURIConverter().getURIMap().put(logical, physical);
            for (String documentListing : List.of("{\"a\":\"a\"}", "[]", "{\"a\":42}", "{\"a\":\"a\",\"a\":\"b\"}", "{\"../a\":\"a\"}")) {
                listing.set(documentListing);
                assertThatThrownBy(() -> new RestfulEMFClient().loadProject(URI.createURI("http://127.0.0.1:" + server.getAddress().getPort() + "/projects/p"), resourceSet))
                        .isInstanceOf(IOException.class);
                assertThat(resourceSet.getResources()).isEmpty();
                assertThat(resourceSet.getPackageRegistry()).containsEntry("preserved", EcorePackage.eINSTANCE);
                assertThat(resourceSet.getURIConverter().getURIMap()).containsEntry(logical, physical).hasSize(1);
                assertThat(resourceSet.getURIConverter().getURIHandlers()).containsExactlyElementsOf(handlers);
            }
        } finally {
            server.stop(0);
        }
    }

    private byte[] binary(XMLResource resource) throws IOException {
        var output = new ByteArrayOutputStream();
        resource.save(output, Map.of(XMLResource.OPTION_BINARY, true));
        return output.toByteArray();
    }
}
