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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.sirius.web.restfulemf.RestfulEMFURIHandler;

import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.json.JsonFactory;

/**
 * Loads a Sirius Web project's semantic documents into an EMF resource set.
 * Resources use public HTTP document URIs and can be saved with {@code resource.save(Map.of())}.
 *
 * @author cbrun
 * @since 2026.7.3
 */
public class RestfulEMFClient {

    private final Map<String, String> headers;

    /**
     * Creates a client without additional HTTP headers.
     */
    public RestfulEMFClient() {
        this(Map.of());
    }

    /**
     * Creates a client with headers scoped to the requested project's REST endpoint.
     *
     * @param headers HTTP headers, for example Authorization or Cookie
     */
    public RestfulEMFClient(Map<String, String> headers) {
        this.headers = Map.copyOf(Objects.requireNonNull(headers));
    }

    /**
     * Loads all semantic documents into a new resource set using downloaded metamodels.
     *
     * @param projectURI a project URL or any project sub-URL
     * @return the loaded resource set
     * @throws IOException if discovery or loading fails
     */
    public ResourceSet loadProject(URI projectURI) throws IOException {
        return this.loadProject(projectURI, new ResourceSetImpl());
    }

    /**
     * Loads into an empty, optionally preconfigured set. Existing package registrations take precedence.
     * Added resources, registrations and URI mappings are removed if loading fails.
     *
     * @param projectURI a project URL or any project sub-URL
     * @param resourceSet an empty resource set with optional package registrations and load options
     * @return the supplied resource set
     * @throws IOException if discovery or loading fails
     */
    // EMF and parser failures must restore the caller's ResourceSet registrations, not leave a partial project.
    @SuppressWarnings("checkstyle:IllegalCatch")
    public ResourceSet loadProject(URI projectURI, ResourceSet resourceSet) throws IOException {
        Objects.requireNonNull(resourceSet);
        if (!resourceSet.getResources().isEmpty()) {
            throw new IllegalArgumentException("The resource set must initially be empty");
        }
        URI endpoint = this.projectEndpoint(Objects.requireNonNull(projectURI));
        var handler = new RestfulEMFURIHandler(endpoint, this.headers);
        var packages = new HashMap<>(resourceSet.getPackageRegistry());
        var mappings = new HashMap<>(resourceSet.getURIConverter().getURIMap());
        resourceSet.getURIConverter().getURIHandlers().add(0, handler);
        try {
            var documents = this.documents(endpoint, handler, resourceSet.getLoadOptions());
            this.registerPackages(endpoint, handler, resourceSet);
            for (String document : documents) {
                URI documentURI = endpoint.appendSegment("documents").appendSegment("bin");
                for (String segment : document.split("/")) {
                    documentURI = documentURI.appendSegment(URI.encodeSegment(segment, false));
                }
                var resource = new XMLResourceImpl(documentURI);
                resource.getDefaultLoadOptions().put(XMLResource.OPTION_BINARY, true);
                resource.getDefaultSaveOptions().put(XMLResource.OPTION_BINARY, true);
                resourceSet.getResources().add(resource);
            }
            for (var resource : List.copyOf(resourceSet.getResources())) {
                resource.load(resourceSet.getLoadOptions());
            }
            return resourceSet;
        } catch (IOException | RuntimeException exception) {
            resourceSet.getResources().clear();
            resourceSet.getPackageRegistry().clear();
            resourceSet.getPackageRegistry().putAll(packages);
            resourceSet.getURIConverter().getURIMap().clear();
            resourceSet.getURIConverter().getURIMap().putAll(mappings);
            resourceSet.getURIConverter().getURIHandlers().remove(handler);
            throw new IOException("Could not load the Sirius Web project", exception);
        }
    }

    private URI projectEndpoint(URI uri) {
        if (!("http".equals(uri.scheme()) || "https".equals(uri.scheme())) || uri.authority() == null || uri.userInfo() != null) {
            throw new IllegalArgumentException("Expected an HTTP(S) Sirius Web project URL without embedded credentials");
        }
        String[] segments = uri.segments();
        for (String segment : segments) {
            this.validateSegment(segment);
        }
        for (int index = 0; index + 1 < segments.length; index++) {
            if ("projects".equals(segments[index]) && !segments[index + 1].isEmpty()) {
                int prefixLength = index;
                if (index >= 2 && "api".equals(segments[index - 2]) && "rest".equals(segments[index - 1])) {
                    prefixLength -= 2;
                }
                URI endpoint = uri.trimFragment().trimQuery().trimSegments(segments.length - prefixLength);
                return endpoint.appendSegments(new String[] { "api", "rest", "projects", segments[index + 1] });
            }
        }
        throw new IllegalArgumentException("The URL must contain /projects/{projectId}");
    }

    private void validateSegment(String segment) {
        String decoded = URI.decode(segment);
        boolean traversal = ".".equals(decoded) || "..".equals(decoded);
        boolean separator = decoded.indexOf('/') >= 0 || decoded.indexOf('\\') >= 0;
        if (traversal || separator
                || decoded.indexOf('%') >= 0 || decoded.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Unsafe URL path segment");
        }
    }

    private List<String> documents(URI endpoint, RestfulEMFURIHandler handler, Map<?, ?> options) throws IOException {
        var paths = new TreeSet<String>();
        try (var input = handler.createInputStream(endpoint.appendSegment("documents"), options);
                var parser = new JsonFactory().createParser(ObjectReadContext.empty(), input)) {
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IOException("Expected a document listing array");
            }
            while (parser.nextToken() == JsonToken.START_OBJECT) {
                String path = null;
                while (parser.nextToken() == JsonToken.PROPERTY_NAME) {
                    String field = parser.currentName();
                    JsonToken value = parser.nextToken();
                    if ("path".equals(field)) {
                        if (path != null || value != JsonToken.VALUE_STRING) {
                            throw new IOException("Invalid document path");
                        }
                        path = parser.getString();
                    } else {
                        parser.skipChildren();
                    }
                }
                if (parser.currentToken() != JsonToken.END_OBJECT || path == null || path.isBlank() || !paths.add(path)) {
                    throw new IOException("Invalid document listing");
                }
                for (String segment : path.split("/", -1)) {
                    this.validateSegment(segment);
                    if (segment.isEmpty()) {
                        throw new IOException("Empty document path segment");
                    }
                }
            }
            if (parser.currentToken() != JsonToken.END_ARRAY || parser.nextToken() != null) {
                throw new IOException("Invalid document listing");
            }
        }
        return List.copyOf(paths);
    }

    private void registerPackages(URI endpoint, RestfulEMFURIHandler handler, ResourceSet target) throws IOException {
        var metadataSet = new ResourceSetImpl();
        metadataSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        var metadata = new XMLResourceImpl(URI.createURI("sirius:///").appendSegment(endpoint.lastSegment()).appendSegment("epackages"));
        metadataSet.getResources().add(metadata);
        try (var input = handler.createInputStream(endpoint.appendSegment("epackages").appendSegment("bin"), target.getLoadOptions())) {
            metadata.load(input, Map.of(XMLResource.OPTION_BINARY, true));
        }
        var downloaded = new ArrayList<EPackage>();
        for (EObject root : metadata.getContents()) {
            if (!(root instanceof EPackage ePackage)) {
                throw new IOException("Expected EPackages in the metamodel response");
            }
            this.collectPackages(ePackage, downloaded);
        }
        this.reconcilePackages(downloaded, target);
        for (EPackage ePackage : downloaded) {
            if (target.getPackageRegistry().getEPackage(ePackage.getNsURI()) == null) {
                target.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
            }
        }
    }

    private void collectPackages(EPackage ePackage, List<EPackage> packages) throws IOException {
        if (ePackage.getNsURI() == null || ePackage.getNsURI().isBlank()) {
            throw new IOException("Downloaded EPackage has no namespace URI");
        }
        packages.add(ePackage);
        for (EPackage nested : ePackage.getESubpackages()) {
            this.collectPackages(nested, packages);
        }
    }

    private void reconcilePackages(List<EPackage> downloaded, ResourceSet target) throws IOException {
        Map<EObject, EObject> replacements = new HashMap<>();
        for (EPackage ePackage : downloaded) {
            EPackage registered = target.getPackageRegistry().getEPackage(ePackage.getNsURI());
            if (registered != null) {
                replacements.put(ePackage, registered);
                var contents = ePackage.eAllContents();
                while (contents.hasNext()) {
                    EObject object = contents.next();
                    if (object instanceof EPackage) {
                        contents.prune();
                    } else {
                        EObject replacement = EcoreUtil.getEObject(registered, EcoreUtil.getRelativeURIFragmentPath(ePackage, object));
                        if (replacement == null) {
                            throw new IOException("Registered metamodel is incompatible with " + ePackage.getNsURI());
                        }
                        replacements.put(object, replacement);
                    }
                }
            }
        }
        var usages = EcoreUtil.UsageCrossReferencer.findAll(replacements.keySet(), downloaded);
        usages.forEach((original, settings) -> settings.forEach(setting -> {
            EObject owner = setting.getEObject();
            while (owner != null && !(owner instanceof EPackage)) {
                owner = owner.eContainer();
            }
            if (owner instanceof EPackage ePackage && target.getPackageRegistry().getEPackage(ePackage.getNsURI()) == null
                    && setting.getEStructuralFeature().isChangeable() && !setting.getEStructuralFeature().isDerived()) {
                EcoreUtil.replace(setting, original, replacements.get(original));
            }
        }));
    }
}
