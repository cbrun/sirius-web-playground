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
package org.eclipse.sirius.web.restfulemf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.sun.net.httpserver.HttpServer;

/**
 * Tests the URI handler over real HTTP connections without a Sirius Web runtime.
 *
 * @author cbrun
 */
public class RestfulEMFURIHandlerTests {

    private static final String HOST = "localhost";

    private static final String ORIGIN_PREFIX = "http://localhost:";

    private static final String PROJECT_ENDPOINT = "/api/rest/projects/project";

    private static final String RELOAD_MESSAGE = "Reload";

    private static final String DOCUMENT_ENDPOINT = "/api/rest/projects/project/documents/bin/document";

    private static final String GET_METHOD = "GET";

    private static final String ETAG_HEADER = "ETag";

    private static final String INITIAL_ETAG = "\"initial\"";

    private static final String IF_MATCH_HEADER = "If-Match";

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BEARER_TOKEN = "Bearer test-token";

    private static final String DOCUMENT_PATH = "/documents/bin/document";

    private static final String UPDATED_ETAG = "\"updated\"";

    private static final String STALE_ETAG = "\"stale\"";

    @Test
    @DisplayName("Given a mapped local resource, when saving through the handler, then nested paths are created conditionally")
    public void givenMappedLocalResourceWhenSavingThenNestedPathsAreCreatedConditionally() throws IOException {
        var server = HttpServer.create(new InetSocketAddress(HOST, 0), 0);
        var requests = new CopyOnWriteArrayList<String>();
        server.createContext("/api/rest/projects/project/documents/xmi/", exchange -> {
            requests.add(exchange.getRequestMethod() + ' ' + exchange.getRequestURI().getRawPath()
                    + ' ' + exchange.getRequestHeaders().getFirst("If-None-Match"));
            exchange.getRequestBody().readAllBytes();
            exchange.sendResponseHeaders(201, -1);
            exchange.close();
        });
        server.start();
        try {
            URI endpoint = URI.createURI(ORIGIN_PREFIX + server.getAddress().getPort() + PROJECT_ENDPOINT);
            var resources = new ResourceSetImpl();
            resources.getURIConverter().getURIHandlers().add(0, new RestfulEMFURIHandler(endpoint, Map.of()));
            resources.getURIConverter().getURIMap().put(URI.createURI("file:/models/"), URI.createURI(endpoint + "/documents/xmi/"));
            var resource = new XMIResourceImpl(URI.createURI("file:/models/domain/model%20one.ecore"));
            resource.getContents().add(EcoreFactory.eINSTANCE.createEClass());
            resources.getResources().add(resource);
            resource.save(Map.of());
            assertThatThrownBy(() -> resource.save(Map.of())).isInstanceOf(IOException.class).hasMessageContaining(RELOAD_MESSAGE);
            assertThat(requests).containsExactly("PUT /api/rest/projects/project/documents/xmi/domain/model%20one.ecore *");
        } finally {
            server.stop(0);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "W/\"weak\"", "*", "unquoted" })
    @DisplayName("Given no valid strong ETag on reload, when saving, then an unprotected write is rejected")
    public void givenNoStrongETagOnReloadWhenSavingThenUnprotectedWriteIsRejected(String entityTag) throws IOException {
        var server = HttpServer.create(new InetSocketAddress(HOST, 0), 0);
        var loads = new AtomicInteger();
        var revisions = new CopyOnWriteArrayList<String>();
        server.createContext(DOCUMENT_ENDPOINT, exchange -> {
            exchange.getRequestBody().readAllBytes();
            if (GET_METHOD.equals(exchange.getRequestMethod())) {
                if (loads.incrementAndGet() == 1) {
                    exchange.getResponseHeaders().set(ETAG_HEADER, INITIAL_ETAG);
                } else if (!entityTag.isEmpty()) {
                    exchange.getResponseHeaders().set(ETAG_HEADER, entityTag);
                }
                exchange.sendResponseHeaders(200, 1);
                exchange.getResponseBody().write(42);
            } else {
                revisions.add(String.valueOf(exchange.getRequestHeaders().getFirst(IF_MATCH_HEADER)));
                exchange.sendResponseHeaders(204, -1);
            }
            exchange.close();
        });
        server.start();
        try {
            var handler = new RestfulEMFURIHandler();
            URI uri = URI.createURI(ORIGIN_PREFIX + server.getAddress().getPort() + DOCUMENT_ENDPOINT);
            for (int index = 0; index < 2; index++) {
                try (var input = handler.createInputStream(uri, Map.of())) {
                    input.readAllBytes();
                }
            }
            assertThatThrownBy(() -> handler.createOutputStream(uri, Map.of()))
                    .isInstanceOf(IOException.class).hasMessageContaining(RELOAD_MESSAGE);
            assertThat(revisions).isEmpty();
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Given an unresponsive endpoint, when a load has an EMF timeout, then it fails with a socket timeout")
    public void givenUnresponsiveEndpointWhenLoadHasEMFTimeoutThenItFails() throws IOException {
        var server = HttpServer.create(new InetSocketAddress(HOST, 0), 0);
        var release = new CountDownLatch(1);
        server.createContext(DOCUMENT_ENDPOINT, exchange -> {
            try {
                release.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            } finally {
                exchange.close();
            }
        });
        server.start();
        try {
            URI uri = URI.createURI(ORIGIN_PREFIX + server.getAddress().getPort() + DOCUMENT_ENDPOINT);
            assertThatThrownBy(() -> new RestfulEMFURIHandler().createInputStream(uri, Map.of(URIConverter.OPTION_TIMEOUT, 100)))
                    .isInstanceOf(SocketTimeoutException.class);
        } finally {
            release.countDown();
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Given invalid headers or unsafe URLs, when authenticated access is configured, then requests are rejected")
    public void givenInvalidHeadersOrUnsafeURLsWhenAuthenticatedAccessIsConfiguredThenRequestsAreRejected() throws IOException {
        var server = HttpServer.create(new InetSocketAddress(HOST, 0), 0);
        var requests = new AtomicInteger();
        server.createContext("/", exchange -> {
            requests.incrementAndGet();
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();
        try {
            URI endpoint = URI.createURI(ORIGIN_PREFIX + server.getAddress().getPort() + PROJECT_ENDPOINT);
            for (String header : new String[] { "iF-mAtCh", "If-None-Match", "Content-Type", "Host", "Content-Length", "Transfer-Encoding", "Bad Header" }) {
                assertThatThrownBy(() -> new RestfulEMFURIHandler(endpoint, Map.of(header, "value")))
                        .isInstanceOf(IllegalArgumentException.class);
            }
            assertThatThrownBy(() -> new RestfulEMFURIHandler(endpoint, Map.of(AUTHORIZATION_HEADER, "token\r\ninjected: value")))
                    .isInstanceOf(IllegalArgumentException.class);
            var handler = new RestfulEMFURIHandler(endpoint, Map.of(AUTHORIZATION_HEADER, BEARER_TOKEN));
            for (String document : new String[] { ".", "..", "%2e%2e", "%2F", "%5c", "%252e%252e", "%00" }) {
                URI unsafe = URI.createURI(endpoint + "/documents/bin/" + document);
                assertThat(handler.canHandle(unsafe)).isFalse();
                assertThatThrownBy(() -> handler.createInputStream(unsafe, Map.of())).isInstanceOf(IOException.class);
                assertThatThrownBy(() -> handler.createOutputStream(unsafe, Map.of())).isInstanceOf(IOException.class);
            }
            URI otherOrigin = URI.createURI(endpoint.toString().replace(HOST, "127.0.0.1") + DOCUMENT_PATH);
            assertThat(handler.canHandle(otherOrigin)).isFalse();
            assertThatThrownBy(() -> handler.createInputStream(otherOrigin, Map.of())).isInstanceOf(IOException.class);
            assertThatThrownBy(() -> handler.createOutputStream(otherOrigin, Map.of())).isInstanceOf(IOException.class);
            assertThat(requests).hasValue(0);
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Given an HTTP access failure, when loading, then the response status is surfaced")
    public void givenHTTPAccessFailureWhenLoadingThenResponseStatusIsSurfaced() throws IOException {
        var server = HttpServer.create(new InetSocketAddress(HOST, 0), 0);
        server.createContext(PROJECT_ENDPOINT, exchange -> {
            String[] segments = exchange.getRequestURI().getPath().split("/");
            exchange.sendResponseHeaders(Integer.parseInt(segments[7]), -1);
            exchange.close();
        });
        server.start();
        try {
            var handler = new RestfulEMFURIHandler();
            for (int status : new int[] { 401, 404 }) {
                URI uri = URI.createURI(ORIGIN_PREFIX + server.getAddress().getPort() + "/api/rest/projects/project/documents/bin/" + status);
                assertThatThrownBy(() -> handler.createInputStream(uri, Map.of()))
                        .isInstanceOf(IOException.class).hasMessageContaining(Integer.toString(status));
            }
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Given an ETag, when resources are saved, then If-Match follows successful revisions and missing tags are cleared")
    public void givenETagWhenResourcesAreSavedThenIfMatchFollowsSuccessfulRevisions() throws IOException {
        var server = HttpServer.create(new InetSocketAddress(HOST, 0), 0);
        var revisions = new CopyOnWriteArrayList<String>();
        var writes = new AtomicInteger();
        server.createContext(DOCUMENT_ENDPOINT, exchange -> {
            exchange.getRequestBody().readAllBytes();
            if (GET_METHOD.equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set(ETAG_HEADER, INITIAL_ETAG);
                exchange.sendResponseHeaders(200, 1);
                exchange.getResponseBody().write(42);
            } else {
                revisions.add(String.valueOf(exchange.getRequestHeaders().getFirst(IF_MATCH_HEADER)));
                if (writes.incrementAndGet() == 1) {
                    exchange.getResponseHeaders().set(ETAG_HEADER, UPDATED_ETAG);
                }
                exchange.sendResponseHeaders(204, -1);
            }
            exchange.close();
        });
        server.start();
        try {
            var handler = new RestfulEMFURIHandler();
            URI uri = URI.createURI(ORIGIN_PREFIX + server.getAddress().getPort() + DOCUMENT_ENDPOINT);
            try (var input = handler.createInputStream(uri, Map.of())) {
                assertThat(input.readAllBytes()).containsExactly((byte) 42);
            }
            for (int index = 0; index < 2; index++) {
                try (var output = handler.createOutputStream(uri, Map.of())) {
                    output.write(42);
                }
            }
            assertThatThrownBy(() -> handler.createOutputStream(uri, Map.of()))
                    .isInstanceOf(IOException.class).hasMessageContaining(RELOAD_MESSAGE);
            assertThat(revisions).containsExactly(INITIAL_ETAG, UPDATED_ETAG);
            try (var input = handler.createInputStream(uri, Map.of())) {
                input.readAllBytes();
            }
            try (var output = handler.createOutputStream(uri, Map.of())) {
                output.write(42);
            }
            assertThat(revisions).containsExactly(INITIAL_ETAG, UPDATED_ETAG, INITIAL_ETAG);
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Given a stale revision, when a save fails, then HTTP 412 is surfaced without retrying or adopting its ETag")
    public void givenStaleRevisionWhenSaveFailsThenHTTP412IsSurfacedWithoutRetry() throws IOException {
        var server = HttpServer.create(new InetSocketAddress(HOST, 0), 0);
        var revisions = new CopyOnWriteArrayList<String>();
        server.createContext(DOCUMENT_ENDPOINT, exchange -> {
            exchange.getRequestBody().readAllBytes();
            if (GET_METHOD.equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set(ETAG_HEADER, STALE_ETAG);
                exchange.sendResponseHeaders(200, 1);
                exchange.getResponseBody().write(42);
            } else {
                revisions.add(exchange.getRequestHeaders().getFirst(IF_MATCH_HEADER));
                exchange.getResponseHeaders().set(ETAG_HEADER, "\"current\"");
                exchange.sendResponseHeaders(412, -1);
            }
            exchange.close();
        });
        server.start();
        try {
            var handler = new RestfulEMFURIHandler();
            URI uri = URI.createURI(ORIGIN_PREFIX + server.getAddress().getPort() + DOCUMENT_ENDPOINT);
            try (var input = handler.createInputStream(uri, Map.of())) {
                input.readAllBytes();
            }
            for (int index = 0; index < 2; index++) {
                assertThatThrownBy(() -> {
                    try (var output = handler.createOutputStream(uri, Map.of())) {
                        output.write(42);
                    }
                }).isInstanceOf(IOException.class).hasMessageContaining("412");
            }
            assertThat(revisions).containsExactly(STALE_ETAG, STALE_ETAG);
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Given authenticated project access, when discovering loading and saving, then headers stay within the project scope")
    public void givenAuthenticatedProjectWhenAccessedThenHeadersStayWithinScope() throws IOException {
        var server = HttpServer.create(new InetSocketAddress(HOST, 0), 0);
        var requests = new CopyOnWriteArrayList<String>();
        server.createContext("/context/api/rest/projects/project", exchange -> {
            requests.add(exchange.getRequestMethod() + ' ' + exchange.getRequestURI().getPath()
                    + ' ' + exchange.getRequestHeaders().getFirst(AUTHORIZATION_HEADER));
            exchange.getRequestBody().readAllBytes();
            byte[] body = "{}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set(ETAG_HEADER, "\"authenticated\"");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        try {
            URI endpoint = URI.createURI(ORIGIN_PREFIX + server.getAddress().getPort() + "/context/api/rest/projects/project");
            var handler = new RestfulEMFURIHandler(endpoint, Map.of(AUTHORIZATION_HEADER, BEARER_TOKEN));
            for (String suffix : new String[] { "/documents", "/epackages/bin", DOCUMENT_PATH }) {
                URI uri = URI.createURI(endpoint + suffix);
                assertThat(handler.canHandle(uri)).isTrue();
                try (var input = handler.createInputStream(uri, Map.of())) {
                    assertThat(input.readAllBytes()).isNotEmpty();
                }
            }
            try (var output = handler.createOutputStream(URI.createURI(endpoint + DOCUMENT_PATH), Map.of())) {
                output.write(42);
            }
            assertThat(requests).hasSize(4).allSatisfy(request -> assertThat(request).endsWith(BEARER_TOKEN));
            URI outside = URI.createURI(endpoint + "-other/documents/bin/document");
            assertThat(handler.canHandle(outside)).isFalse();
            assertThatThrownBy(() -> handler.createInputStream(outside, Map.of())).isInstanceOf(IOException.class);
            assertThat(requests).hasSize(4);
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Given an HTTP redirect, when loading or saving, then credentials are not forwarded")
    public void givenHTTPRedirectWhenLoadingOrSavingThenCredentialsAreNotForwarded() throws IOException {
        var server = HttpServer.create(new InetSocketAddress(HOST, 0), 0);
        var redirectedRequests = new AtomicInteger();
        server.createContext(DOCUMENT_ENDPOINT, exchange -> {
            exchange.getRequestBody().readAllBytes();
            exchange.getResponseHeaders().set("Location", "/outside");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        });
        server.createContext("/outside", exchange -> {
            redirectedRequests.incrementAndGet();
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();
        try {
            URI endpoint = URI.createURI(ORIGIN_PREFIX + server.getAddress().getPort() + PROJECT_ENDPOINT);
            var handler = new RestfulEMFURIHandler(endpoint, Map.of(AUTHORIZATION_HEADER, BEARER_TOKEN));
            assertThatThrownBy(() -> handler.createInputStream(URI.createURI(endpoint + DOCUMENT_PATH), Map.of()))
                    .isInstanceOf(IOException.class).hasMessageContaining("302");
            assertThatThrownBy(() -> {
                try (var output = handler.createOutputStream(URI.createURI(endpoint + DOCUMENT_PATH), Map.of())) {
                    output.write(42);
                }
            }).isInstanceOf(IOException.class).hasMessageContaining("302");
            assertThat(redirectedRequests).hasValue(0);
        } finally {
            server.stop(0);
        }
    }
}
