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
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;

/**
 * An EMF HTTP URI handler which propagates RESTful EMF entity tags from loads to saves.
 *
 * @since 2026.7.3
 */
public class RestfulEMFURIHandler extends URIHandlerImpl {

    private static final String ETAG = "ETag";

    private static final String IF_MATCH = "If-Match";

    private final Map<URI, String> entityTags = new ConcurrentHashMap<>();

    @Override
    public boolean canHandle(URI uri) {
        String path = uri.path();
        return ("http".equals(uri.scheme()) || "https".equals(uri.scheme()))
                && path != null
                && path.startsWith("/api/rest/projects/")
                && (path.endsWith("/xmi") || path.endsWith("/xmi.zip") || path.endsWith("/bin"));
    }

    @Override
    public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
        HttpURLConnection connection = this.openConnection(uri, options);
        connection.setRequestMethod("GET");
        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            connection.disconnect();
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
    }

    @Override
    public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException {
        HttpURLConnection connection = this.openConnection(uri, options);
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(64 * 1024);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        String entityTag = this.entityTags.get(uri);
        if (entityTag != null) {
            connection.setRequestProperty(IF_MATCH, entityTag);
        }
        return new FilterOutputStream(connection.getOutputStream()) {
            @Override
            public void close() throws IOException {
                IOException failure = null;
                try {
                    super.close();
                    int status = connection.getResponseCode();
                    if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_CREATED || status == HttpURLConnection.HTTP_NO_CONTENT) {
                        RestfulEMFURIHandler.this.rememberEntityTag(uri, connection);
                    } else {
                        failure = new IOException("PUT failed with HTTP response code " + status);
                    }
                } catch (IOException exception) {
                    failure = exception;
                } finally {
                    connection.disconnect();
                }
                if (failure != null) {
                    throw failure;
                }
            }
        };
    }

    private HttpURLConnection openConnection(URI uri, Map<?, ?> options) throws IOException {
        var connection = (HttpURLConnection) java.net.URI.create(uri.toString()).toURL().openConnection();
        int timeout = this.getTimeout(options);
        if (timeout != 0) {
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
        }
        return connection;
    }

    private void rememberEntityTag(URI uri, HttpURLConnection connection) {
        String entityTag = connection.getHeaderField(ETAG);
        if (entityTag != null) {
            this.entityTags.put(uri, entityTag);
        }
    }
}
