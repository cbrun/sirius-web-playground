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

import java.util.List;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.services.DetachedResourceSet;
import org.eclipse.sirius.web.restfulemf.services.ResourceReferences;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;
import org.junit.jupiter.api.Test;

/**
 * Checks pending-path validation and reference translation without demand loading.
 */
public class ResourceReferencesTests {

    private static final String SOURCE_PATH = "source.ecore";

    private static final String DOCUMENT_BASE = "restfulemf:/documents/";

    @Test
    public void invalidPendingAndUnregisteredExternalReferencesAreRejected() {
        var document = new ResourceDocument(UUID.randomUUID(), SOURCE_PATH, false);
        var resources = new DetachedResourceSet();
        var service = new ResourceReferences();
        for (String uri : List.of(DOCUMENT_BASE + "../outside.ecore#/", DOCUMENT_BASE + "%2e%2e/outside.ecore#/",
                DOCUMENT_BASE + "a%2fb.ecore#/", DOCUMENT_BASE + "a%5cb.ecore#/", DOCUMENT_BASE + "a%252fb.ecore#/",
                DOCUMENT_BASE + "a.ecore?q=1#/", "restfulemf://host/documents/a.ecore#/", "restfulemf:/outside/a.ecore#/",
                "https://example.invalid/model.ecore#/", "file:/etc/model.ecore#/", "pathmap://UNKNOWN/model.ecore#/")) {
            var resource = new ResourceImpl(service.publicURI(document));
            var proxy = EcoreFactory.eINSTANCE.createEClass();
            ((InternalEObject) proxy).eSetProxyURI(URI.createURI(uri));
            resource.getContents().add(proxy);
            assertThatThrownBy(() -> service.canonicalize(resource, document, List.of(document), resources)).isInstanceOf(RestfulEMFException.class);
            assertThat(resources.getResources()).isEmpty();
        }
    }

    @Test
    public void containmentProxiesBindWithoutResolutionAndReadonlySourcesStayUnchanged() {
        var source = new ResourceDocument(UUID.randomUUID(), SOURCE_PATH, true);
        var target = new ResourceDocument(UUID.randomUUID(), "école/model space.ecore", false);
        var service = new ResourceReferences();
        var resources = new DetachedResourceSet();
        var factory = new JSONResourceFactory();
        var targetResource = new ResourceImpl(factory.createResourceURI(target.id().toString()));
        targetResource.getContents().add(EcoreFactory.eINSTANCE.createEClass());
        var sourceResource = new ResourceImpl(factory.createResourceURI(source.id().toString()));
        var annotation = EcoreFactory.eINSTANCE.createEAnnotation();
        var proxy = EcoreFactory.eINSTANCE.createEClass();
        URI pending = service.publicURI(target).appendFragment("/");
        ((InternalEObject) proxy).eSetProxyURI(pending);
        annotation.getContents().add(proxy);
        sourceResource.getContents().add(annotation);
        resources.getResources().addAll(List.of(sourceResource, targetResource));

        service.reconcile(resources, List.of(source, target));
        assertThat(((InternalEObject) proxy).eProxyURI()).isEqualTo(pending);
        service.reconcile(resources, List.of(new ResourceDocument(source.id(), SOURCE_PATH, false), target));
        assertThat(proxy.eIsProxy()).isTrue();
        assertThat(((InternalEObject) proxy).eProxyURI()).isEqualTo(targetResource.getURI().appendFragment("/"));
        assertThat(resources.getResources()).hasSize(2);
    }

    @Test
    public void registeredMetamodelAndLoadedExternalResourceReferencesRemainAllowed() {
        var document = new ResourceDocument(UUID.randomUUID(), SOURCE_PATH, false);
        var resources = new DetachedResourceSet();
        resources.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        URI external = URI.createURI("https://example.invalid/already-loaded.ecore");
        var loaded = new ResourceImpl(external);
        loaded.getContents().add(EcoreFactory.eINSTANCE.createEClass());
        resources.getResources().add(loaded);
        var service = new ResourceReferences();
        for (URI uri : List.of(external.appendFragment("/"), URI.createURI(EcorePackage.eNS_URI).appendFragment("//EClass"))) {
            var resource = new ResourceImpl(service.publicURI(document));
            var proxy = EcoreFactory.eINSTANCE.createEClass();
            ((InternalEObject) proxy).eSetProxyURI(uri);
            resource.getContents().add(proxy);
            service.canonicalize(resource, document, List.of(document), resources);
            assertThat(((InternalEObject) proxy).eProxyURI()).isEqualTo(uri);
        }
        assertThat(resources.getResources()).containsExactly(loaded);
    }
}
