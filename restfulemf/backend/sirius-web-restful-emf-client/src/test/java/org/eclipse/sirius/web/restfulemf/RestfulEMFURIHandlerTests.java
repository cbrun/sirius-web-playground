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
import org.eclipse.emf.ecore.resource.URIConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

/**
 * Tests the URI handler over real HTTP connections without a Sirius Web runtime.
 */
public class RestfulEMFURIHandlerTests {

    @Test
    @DisplayName("Given a missing ETag on reload, when saving, then the previous revision is not sent")
    public void givenMissingETagOnReloadWhenSavingThenPreviousRevisionIsNotSent() throws IOException {
        var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        var loads = new AtomicInteger();
        var revisions = new CopyOnWriteArrayList<String>();
        server.createContext("/api/rest/projects/project/document/bin", exchange -> {
            exchange.getRequestBody().readAllBytes();
            if ("GET".equals(exchange.getRequestMethod())) {
                if (loads.incrementAndGet() == 1) {
                    exchange.getResponseHeaders().set("ETag", "\"initial\"");
                }
                exchange.sendResponseHeaders(200, 1);
                exchange.getResponseBody().write(42);
            } else {
                revisions.add(String.valueOf(exchange.getRequestHeaders().getFirst("If-Match")));
                exchange.sendResponseHeaders(204, -1);
            }
            exchange.close();
        });
        server.start();
        try {
            var handler = new RestfulEMFURIHandler();
            URI uri = URI.createURI("http://localhost:" + server.getAddress().getPort() + "/api/rest/projects/project/document/bin");
            for (int index = 0; index < 2; index++) {
                try (var input = handler.createInputStream(uri, Map.of())) {
                    input.readAllBytes();
                }
            }
            try (var output = handler.createOutputStream(uri, Map.of())) {
                output.write(42);
            }
            assertThat(revisions).containsExactly("null");
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Given an unresponsive endpoint, when a load has an EMF timeout, then it fails with a socket timeout")
    public void givenUnresponsiveEndpointWhenLoadHasEMFTimeoutThenItFails() throws IOException {
        var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        var release = new CountDownLatch(1);
        server.createContext("/api/rest/projects/project/document/bin", exchange -> {
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
            URI uri = URI.createURI("http://localhost:" + server.getAddress().getPort() + "/api/rest/projects/project/document/bin");
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
        var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        var requests = new AtomicInteger();
        server.createContext("/", exchange -> {
            requests.incrementAndGet();
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();
        try {
            URI endpoint = URI.createURI("http://localhost:" + server.getAddress().getPort() + "/api/rest/projects/project");
            for (String header : new String[] { "iF-mAtCh", "Content-Type", "Host", "Content-Length", "Transfer-Encoding", "Bad Header" }) {
                assertThatThrownBy(() -> new RestfulEMFURIHandler(endpoint, Map.of(header, "value")))
                        .isInstanceOf(IllegalArgumentException.class);
            }
            assertThatThrownBy(() -> new RestfulEMFURIHandler(endpoint, Map.of("Authorization", "token\r\ninjected: value")))
                    .isInstanceOf(IllegalArgumentException.class);
            var handler = new RestfulEMFURIHandler(endpoint, Map.of("Authorization", "Bearer test-token"));
            for (String document : new String[] { ".", "..", "%2e%2e", "%2F", "%5c", "%252e%252e", "%00" }) {
                URI unsafe = URI.createURI(endpoint + "/" + document + "/bin");
                assertThat(handler.canHandle(unsafe)).isFalse();
                assertThatThrownBy(() -> handler.createInputStream(unsafe, Map.of())).isInstanceOf(IOException.class);
                assertThatThrownBy(() -> handler.createOutputStream(unsafe, Map.of())).isInstanceOf(IOException.class);
            }
            URI otherOrigin = URI.createURI(endpoint.toString().replace("localhost", "127.0.0.1") + "/document/bin");
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
        var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/api/rest/projects/project", exchange -> {
            String[] segments = exchange.getRequestURI().getPath().split("/");
            exchange.sendResponseHeaders(Integer.parseInt(segments[5]), -1);
            exchange.close();
        });
        server.start();
        try {
            var handler = new RestfulEMFURIHandler();
            for (int status : new int[] { 401, 404 }) {
                URI uri = URI.createURI("http://localhost:" + server.getAddress().getPort() + "/api/rest/projects/project/" + status + "/bin");
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
        var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        var revisions = new CopyOnWriteArrayList<String>();
        var writes = new AtomicInteger();
        server.createContext("/api/rest/projects/project/document/bin", exchange -> {
            exchange.getRequestBody().readAllBytes();
            if ("GET".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("ETag", "\"initial\"");
                exchange.sendResponseHeaders(200, 1);
                exchange.getResponseBody().write(42);
            } else {
                revisions.add(String.valueOf(exchange.getRequestHeaders().getFirst("If-Match")));
                if (writes.incrementAndGet() == 1) {
                    exchange.getResponseHeaders().set("ETag", "\"updated\"");
                }
                exchange.sendResponseHeaders(204, -1);
            }
            exchange.close();
        });
        server.start();
        try {
            var handler = new RestfulEMFURIHandler();
            URI uri = URI.createURI("http://localhost:" + server.getAddress().getPort() + "/api/rest/projects/project/document/bin");
            try (var input = handler.createInputStream(uri, Map.of())) {
                assertThat(input.readAllBytes()).containsExactly((byte) 42);
            }
            for (int index = 0; index < 3; index++) {
                try (var output = handler.createOutputStream(uri, Map.of())) {
                    output.write(42);
                }
            }
            assertThat(revisions).containsExactly("\"initial\"", "\"updated\"", "null");
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Given a stale revision, when a save fails, then HTTP 412 is surfaced without retrying or adopting its ETag")
    public void givenStaleRevisionWhenSaveFailsThenHTTP412IsSurfacedWithoutRetry() throws IOException {
        var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        var revisions = new CopyOnWriteArrayList<String>();
        server.createContext("/api/rest/projects/project/document/bin", exchange -> {
            exchange.getRequestBody().readAllBytes();
            if ("GET".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("ETag", "\"stale\"");
                exchange.sendResponseHeaders(200, 1);
                exchange.getResponseBody().write(42);
            } else {
                revisions.add(exchange.getRequestHeaders().getFirst("If-Match"));
                exchange.getResponseHeaders().set("ETag", "\"current\"");
                exchange.sendResponseHeaders(412, -1);
            }
            exchange.close();
        });
        server.start();
        try {
            var handler = new RestfulEMFURIHandler();
            URI uri = URI.createURI("http://localhost:" + server.getAddress().getPort() + "/api/rest/projects/project/document/bin");
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
            assertThat(revisions).containsExactly("\"stale\"", "\"stale\"");
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Given authenticated project access, when discovering loading and saving, then headers stay within the project scope")
    public void givenAuthenticatedProjectWhenAccessedThenHeadersStayWithinScope() throws IOException {
        var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        var requests = new CopyOnWriteArrayList<String>();
        server.createContext("/context/api/rest/projects/project", exchange -> {
            requests.add(exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath()
                    + " " + exchange.getRequestHeaders().getFirst("Authorization"));
            exchange.getRequestBody().readAllBytes();
            byte[] body = "{}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        try {
            URI endpoint = URI.createURI("http://localhost:" + server.getAddress().getPort() + "/context/api/rest/projects/project");
            var handler = new RestfulEMFURIHandler(endpoint, Map.of("Authorization", "Bearer test-token"));
            for (String suffix : new String[] { "/documents", "/epackages/bin", "/document/bin" }) {
                URI uri = URI.createURI(endpoint + suffix);
                assertThat(handler.canHandle(uri)).isTrue();
                try (var input = handler.createInputStream(uri, Map.of())) {
                    assertThat(input.readAllBytes()).isNotEmpty();
                }
            }
            try (var output = handler.createOutputStream(URI.createURI(endpoint + "/document/bin"), Map.of())) {
                output.write(42);
            }
            assertThat(requests).hasSize(4).allSatisfy(request -> assertThat(request).endsWith("Bearer test-token"));
            URI outside = URI.createURI(endpoint + "-other/document/bin");
            assertThat(handler.canHandle(outside)).isFalse();
            assertThatThrownBy(() -> handler.createInputStream(outside, Map.of())).isInstanceOf(IOException.class);
            assertThat(requests).hasSize(4);
        } finally {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Given an HTTP redirect, when loading, then the redirect is rejected without forwarding credentials")
    public void givenHTTPRedirectWhenLoadingThenCredentialsAreNotForwarded() throws IOException {
        var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        var redirectedRequests = new AtomicInteger();
        server.createContext("/api/rest/projects/project/document/bin", exchange -> {
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
            URI endpoint = URI.createURI("http://localhost:" + server.getAddress().getPort() + "/api/rest/projects/project");
            var handler = new RestfulEMFURIHandler(endpoint, Map.of("Authorization", "Bearer test-token"));
            assertThatThrownBy(() -> handler.createInputStream(URI.createURI(endpoint + "/document/bin"), Map.of()))
                    .isInstanceOf(IOException.class).hasMessageContaining("302");
            assertThat(redirectedRequests).hasValue(0);
        } finally {
            server.stop(0);
        }
    }
}
