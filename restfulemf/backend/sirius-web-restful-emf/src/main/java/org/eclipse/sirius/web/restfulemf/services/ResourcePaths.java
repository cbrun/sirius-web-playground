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
package org.eclipse.sirius.web.restfulemf.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;
import org.springframework.stereotype.Service;

/**
 * Assigns public paths from document names without additional persistent metadata.
 *
 * @author cbrun
 */
@Service
public class ResourcePaths {

    private static final String ID_PATH_PREFIX = "_by-id/";

    public List<ResourceDocument> assignPaths(List<ResourceDocument> documents) {
        var counts = documents.stream().map(ResourceDocument::name).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return documents.stream().map(document -> {
            String path = ID_PATH_PREFIX + document.id();
            if (this.isValid(document.name()) && !document.name().startsWith(ID_PATH_PREFIX) && !document.name().equals("_by-id") && counts.get(document.name()) == 1) {
                path = document.name();
            }
            return new ResourceDocument(document.id(), document.name(), path, document.readOnly());
        }).sorted(Comparator.comparing(ResourceDocument::path)).toList();
    }

    public Optional<ResourceDocument> find(List<ResourceDocument> documents, String path) {
        this.validate(path);
        if (!path.startsWith(ID_PATH_PREFIX) && documents.stream().filter(document -> document.name().equals(path)).count() > 1) {
            throw new RestfulEMFException(RestfulEMFError.CONFLICT, "The document path is ambiguous");
        }
        return this.assignPaths(documents).stream().filter(document -> document.path().equals(path)).findFirst();
    }

    public void validate(String path) {
        if (!this.isValid(path)) {
            throw new RestfulEMFException(RestfulEMFError.INVALID_RESOURCE, "Invalid relative document path");
        }
    }

    public List<ResourceDocument> documents(ResourceSet resourceSet) {
        List<ResourceDocument> documents = new ArrayList<>();
        for (var resource : resourceSet.getResources()) {
            var uri = resource.getURI();
            if (uri != null && "sirius".equals(uri.scheme()) && uri.segmentCount() == 1) {
                try {
                    UUID id = UUID.fromString(uri.lastSegment());
                    resource.eAdapters().stream().filter(ResourceMetadataAdapter.class::isInstance).map(ResourceMetadataAdapter.class::cast).findFirst()
                            .ifPresent(adapter -> documents.add(new ResourceDocument(id, adapter.getName(), adapter.isReadOnly())));
                } catch (IllegalArgumentException exception) {
                    // Non-document resources are not exposed through the project document API.
                }
            }
        }
        return this.assignPaths(documents);
    }

    private boolean isValid(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        return path.codePoints().noneMatch(character -> "\\%?#;".indexOf(character) >= 0 || Character.isISOControl(character))
                && Arrays.stream(path.split("/", -1)).noneMatch(segment -> segment.isEmpty() || segment.equals(".") || segment.equals(".."));
    }
}
