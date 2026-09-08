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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;

/**
 * Translates document references without resolving proxies or loading external resources.
 * Pending paths are stored as ordinary proxy URIs in the document content.
 */
public class ResourceReferences {

    private static final URI DOCUMENT_BASE = URI.createURI("restfulemf:/documents/");

    public URI publicURI(ResourceDocument document) {
        URI uri = DOCUMENT_BASE.trimSegments(1);
        for (String segment : document.path().split("/")) {
            uri = uri.appendSegment(URI.encodeSegment(segment, false));
        }
        return uri;
    }

    public String objectId(UUID documentId, String fragment) {
        return UUID.nameUUIDFromBytes((documentId + "\u0000" + fragment).getBytes(StandardCharsets.UTF_8)).toString();
    }

    public void canonicalize(Resource resource, ResourceDocument document, List<ResourceDocument> documents, ResourceSet existingResources) {
        Map<URI, ResourceDocument> paths = this.paths(documents);
        paths.put(this.publicURI(document), document);
        Map<URI, Resource> resources = this.resources(existingResources);
        resources.put(this.canonicalURI(document), resource);
        this.forEachReference(resource, proxy -> {
            URI targetURI = proxy.eIsProxy() ? proxy.eProxyURI() : proxy.eResource() == null ? null : proxy.eResource().getURI();
            if (targetURI != null && (DOCUMENT_BASE.scheme().equals(targetURI.scheme()) || "sirius".equals(targetURI.scheme()))) {
                this.validateReferenceTarget(proxy);
            }
            if (!proxy.eIsProxy()) {
                return;
            }
            URI uri = proxy.eProxyURI();
            Resource loadedResource = resources.get(uri.trimFragment());
            if (!DOCUMENT_BASE.scheme().equals(uri.scheme()) && !"sirius".equals(uri.scheme())
                    && !existingResources.getPackageRegistry().containsKey(uri.trimFragment().toString())
                    && !EPackage.Registry.INSTANCE.containsKey(uri.trimFragment().toString())
                    && (loadedResource == null || !loadedResource.isLoaded() && loadedResource.getContents().isEmpty())) {
                throw new RestfulEMFException(RestfulEMFError.INVALID_RESOURCE, "An unresolved reference targets an external resource");
            }
            this.bind(proxy, paths, resources);
        });
    }

    public void reconcile(ResourceSet resourceSet, List<ResourceDocument> documents) {
        Map<URI, ResourceDocument> paths = this.paths(documents);
        Map<URI, Resource> resources = this.resources(resourceSet);
        var readOnly = documents.stream().filter(ResourceDocument::readOnly).map(this::canonicalURI).collect(Collectors.toSet());
        // ponytail: one project scan per import; add a pending-reference index only if profiling warrants it.
        for (Resource resource : List.copyOf(resourceSet.getResources())) {
            if (!readOnly.contains(resource.getURI())) {
                this.forEachProxy(resource, proxy -> this.bind(proxy, paths, resources));
            }
        }
    }

    public void externalize(Resource resource, ResourceDocument document, List<ResourceDocument> documents) {
        Map<URI, URI> paths = new HashMap<>();
        for (ResourceDocument entry : documents) {
            paths.put(this.canonicalURI(entry), this.publicURI(entry));
        }
        this.forEachProxy(resource, proxy -> {
            URI uri = proxy.eProxyURI();
            URI path = paths.get(uri.trimFragment());
            if (path != null) {
                proxy.eSetProxyURI(path.appendFragment(uri.fragment()));
            }
        });
        resource.setURI(this.publicURI(document));
    }

    private void bind(InternalEObject proxy, Map<URI, ResourceDocument> paths, Map<URI, Resource> resources) {
        URI uri = proxy.eProxyURI();
        if (DOCUMENT_BASE.scheme().equals(uri.scheme())) {
            uri = this.validatePendingURI(uri);
            proxy.eSetProxyURI(uri);
            ResourceDocument targetDocument = paths.get(uri.trimFragment());
            if (targetDocument != null) {
                Resource target = resources.get(this.canonicalURI(targetDocument));
                if (target != null) {
                    EObject object = this.findObject(target, targetDocument.id(), uri.fragment());
                    if (object != null) {
                        this.validateReferenceTarget(object);
                        proxy.eSetProxyURI(this.canonicalURI(targetDocument).appendFragment(target.getURIFragment(object)));
                    }
                }
            }
        }
    }

    private void validateReferenceTarget(EObject target) {
        if (target instanceof EAnnotation || target instanceof EOperation || target instanceof EParameter || target instanceof EGenericType
                || target instanceof ETypeParameter || target instanceof EEnumLiteral || target instanceof EPackage ePackage && ePackage.getESuperPackage() != null
                || EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY.isInstance(target)) {
            throw new RestfulEMFException(RestfulEMFError.INVALID_RESOURCE,
                    "References to this Ecore object type cannot survive the upstream JSON persistence format: " + target.eClass().getName());
        }
    }

    private URI validatePendingURI(URI uri) {
        if (uri.authority() != null || uri.query() != null || uri.device() != null || !uri.hasAbsolutePath()
                || uri.segmentCount() < 2 || !"documents".equals(uri.segment(0))) {
            throw new RestfulEMFException(RestfulEMFError.INVALID_RESOURCE, "A relative reference escapes the project document space");
        }
        URI normalized = DOCUMENT_BASE.trimSegments(1);
        for (int index = 1; index < uri.segmentCount(); index++) {
            String segment = URI.decode(uri.segment(index));
            new ResourcePaths().validate(segment);
            if (segment.contains("/")) {
                throw new RestfulEMFException(RestfulEMFError.INVALID_RESOURCE, "Encoded reference path separators are not allowed");
            }
            normalized = normalized.appendSegment(URI.encodeSegment(segment, false));
        }
        return normalized.appendFragment(uri.fragment());
    }

    private EObject findObject(Resource resource, UUID documentId, String fragment) {
        EObject result = null;
        if (fragment != null && !fragment.isEmpty() && !fragment.contains("/-1")) {
            result = resource.getEObject(fragment);
            if (result == null) {
                var objects = EcoreUtil.<EObject>getAllProperContents(resource, false);
                while (result == null && objects.hasNext()) {
                    EObject object = objects.next();
                    if (!object.eIsProxy() && fragment.equals(EcoreUtil.getID(object))) {
                        result = object;
                    }
                }
            }
            if (result == null) {
                result = resource.getEObject(this.objectId(documentId, fragment));
            }
            if (result == null && fragment.startsWith("/?")) {
                int separator = fragment.indexOf('/', 2);
                if (separator > 2) {
                    EObject anchor = this.findObject(resource, documentId, fragment.substring(2, separator));
                    if (anchor != null) {
                        result = resource.getEObject("/?" + resource.getURIFragment(anchor) + fragment.substring(separator));
                    }
                }
            }
        }
        return result;
    }

    private Map<URI, ResourceDocument> paths(List<ResourceDocument> documents) {
        Map<URI, ResourceDocument> paths = new HashMap<>();
        documents.forEach(document -> paths.put(this.publicURI(document), document));
        return paths;
    }

    private Map<URI, Resource> resources(ResourceSet resourceSet) {
        Map<URI, Resource> resources = new HashMap<>();
        resourceSet.getResources().forEach(resource -> resources.put(resource.getURI(), resource));
        return resources;
    }

    private URI canonicalURI(ResourceDocument document) {
        return new JSONResourceFactory().createResourceURI(document.id().toString());
    }

    private void forEachProxy(Resource resource, Consumer<InternalEObject> consumer) {
        this.forEachReference(resource, target -> {
            if (target.eIsProxy()) {
                consumer.accept(target);
            }
        });
    }

    private void forEachReference(Resource resource, Consumer<InternalEObject> consumer) {
        var objects = EcoreUtil.<EObject>getAllProperContents(resource, false);
        while (objects.hasNext()) {
            EObject object = objects.next();
            if (object.eIsProxy() && object instanceof InternalEObject proxy) {
                consumer.accept(proxy);
                continue;
            }
            for (var reference : object.eClass().getEAllReferences()) {
                if (!reference.isContainment() && !reference.isContainer() && !reference.isDerived() && !reference.isTransient()) {
                    Object value = object.eGet(reference, false);
                    if (value instanceof InternalEList<?> values) {
                        values.basicIterator().forEachRemaining(target -> {
                            if (target instanceof InternalEObject proxy) {
                                consumer.accept(proxy);
                            }
                        });
                    } else if (value instanceof InternalEObject proxy) {
                        consumer.accept(proxy);
                    }
                }
            }
        }
    }
}
