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

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;

/**
 * An EMF HTTP URI handler which propagates RESTful EMF entity tags from loads to saves.
 *
 * @author cbrun
 * @since 2026.7.3
 */
public class RestfulEMFURIHandler extends URIHandlerImpl {

    private static final String ETAG = "ETag";

    private static final String IF_MATCH = "If-Match";

    private static final String IF_NONE_MATCH = "If-None-Match";

    private static final String RELOAD_REQUIRED = "";

    private static final Set<String> RESERVED_HEADERS = Set.of("if-match", "if-none-match", "content-type", "content-length", "transfer-encoding", "host");

    private final Map<URI, String> entityTags = new ConcurrentHashMap<>();

    private final URI projectEndpoint;

    private final Map<String, String> headers;

    /**
     * Creates an unauthenticated handler for RESTful EMF endpoints.
     */
    public RestfulEMFURIHandler() {
        this.projectEndpoint = null;
        this.headers = Map.of();
    }

    /**
     * Creates a handler restricted to one REST project endpoint. Redirects are not followed.
     *
     * @param projectEndpoint absolute HTTP(S) URI ending in {@code /api/rest/projects/PROJECT_ID}
     * @param headers request headers, such as Authorization or Cookie; protocol headers are managed by this handler
     */
    public RestfulEMFURIHandler(URI projectEndpoint, Map<String, String> headers) {
        this.projectEndpoint = Objects.requireNonNull(projectEndpoint);
        this.headers = Map.copyOf(Objects.requireNonNull(headers));
        if (!this.isHttpURI(projectEndpoint) || projectEndpoint.query() != null || projectEndpoint.fragment() != null
                || !projectEndpoint.path().matches("(?:/[^/]+)*/api/rest/projects/[^/]+")) {
            throw new IllegalArgumentException("An absolute RESTful EMF project endpoint is required");
        }
        this.headers.forEach((name, value) -> {
            if (!name.matches("[!#$%&'*+.^_`|~0-9A-Za-z-]+") || value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0
                    || RESERVED_HEADERS.contains(name.toLowerCase(java.util.Locale.ROOT))) {
                throw new IllegalArgumentException("Invalid or reserved HTTP request header");
            }
        });
    }

    @Override
    public boolean canHandle(URI uri) {
        String path = uri.path();
        boolean withinProject = true;
        if (this.projectEndpoint != null) {
            withinProject = Objects.equals(uri.scheme(), this.projectEndpoint.scheme())
                    && Objects.equals(uri.authority(), this.projectEndpoint.authority())
                    && path != null && path.startsWith(this.projectEndpoint.path() + "/");
        }
        return withinProject && this.isHttpURI(uri) && path != null
                && path.matches("(?:/[^/]+)*/api/rest/projects/[^/]+/(?:documents(?:/(?:xmi|xmi\\.zip|bin)/[^/]+(?:/[^/]+)*)?|epackages/(?:xmi|bin))");
    }

    private boolean isHttpURI(URI uri) {
        if (!("http".equals(uri.scheme()) || "https".equals(uri.scheme())) || uri.authority() == null || uri.userInfo() != null) {
            return false;
        }
        boolean safePath = true;
        for (String segment : uri.segments()) {
            String decoded = URI.decode(segment);
            boolean traversal = ".".equals(decoded) || "..".equals(decoded);
            boolean separator = decoded.contains("/") || decoded.contains("\\");
            if (traversal || separator
                    || decoded.contains("%") || decoded.chars().anyMatch(Character::isISOControl)) {
                safePath = false;
                break;
            }
        }
        return safePath;
    }

    @Override
    // HTTP callbacks may fail unchecked; disconnect only before stream ownership reaches the caller.
    @SuppressWarnings("checkstyle:IllegalCatch")
    public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
        HttpURLConnection connection = this.openConnection(uri, options);
        try {
            connection.setRequestMethod("GET");
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IOException("GET failed with HTTP response code " + status);
            }
            this.rememberEntityTag(uri, connection);
            return new FilterInputStream(connection.getInputStream()) {
                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    } finally {
                        connection.disconnect();
                    }
                }
            };
        } catch (IOException | RuntimeException exception) {
            connection.disconnect();
            throw exception;
        }
    }

    @Override
    // HTTP callbacks may fail unchecked; successful request streams must remain open for the caller.
    @SuppressWarnings("checkstyle:IllegalCatch")
    public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException {
        String entityTag = this.entityTags.get(uri);
        if (RELOAD_REQUIRED.equals(entityTag)) {
            throw new IOException("Reload the resource before saving again: no current strong ETag is available");
        }
        HttpURLConnection connection = this.openConnection(uri, options);
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(64 * 1024);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        if (entityTag != null) {
            connection.setRequestProperty(IF_MATCH, entityTag);
        } else {
            connection.setRequestProperty(IF_NONE_MATCH, "*");
        }
        OutputStream output;
        try {
            output = connection.getOutputStream();
        } catch (IOException | RuntimeException exception) {
            connection.disconnect();
            throw exception;
        }
        return new FilterOutputStream(output) {
            @Override
            public void write(byte[] bytes, int offset, int length) throws IOException {
                this.out.write(bytes, offset, length);
            }

            @Override
            public void close() throws IOException {
                try {
                    super.close();
                    int status = connection.getResponseCode();
                    if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_CREATED || status == HttpURLConnection.HTTP_NO_CONTENT) {
                        RestfulEMFURIHandler.this.rememberEntityTag(uri, connection);
                    } else {
                        throw new IOException("PUT failed with HTTP response code " + status);
                    }
                } finally {
                    connection.disconnect();
                }
            }
        };
    }

    private HttpURLConnection openConnection(URI uri, Map<?, ?> options) throws IOException {
        if (!this.canHandle(uri)) {
            throw new IOException("URI is outside this RESTful EMF handler's scope");
        }
        var connection = (HttpURLConnection) java.net.URI.create(uri.toString()).toURL().openConnection();
        connection.setInstanceFollowRedirects(false);
        this.headers.forEach(connection::setRequestProperty);
        int timeout = this.getTimeout(options);
        if (timeout != 0) {
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
        }
        return connection;
    }

    private void rememberEntityTag(URI uri, HttpURLConnection connection) {
        String entityTag = connection.getHeaderField(ETAG);
        // Only the entity-tag syntax is valid here: accepting "*" would disable revision matching on PUT.
        if (entityTag != null && entityTag.matches("\"[\\x21\\x23-\\x7E\\x80-\\xFF]*\"")) {
            this.entityTags.put(uri, entityTag);
        } else {
            this.entityTags.put(uri, RELOAD_REQUIRED);
        }
    }
}
