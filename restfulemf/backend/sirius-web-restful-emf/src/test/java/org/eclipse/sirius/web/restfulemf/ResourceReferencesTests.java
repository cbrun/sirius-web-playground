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
 *
 * @author cbrun
 */
public class ResourceReferencesTests {

    private static final String ROOT_FRAGMENT = "/";

    private static final String TARGET_PATH = "target.ecore";

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
            var owner = EcoreFactory.eINSTANCE.createEClass();
            owner.getESuperTypes().add(proxy);
            resource.getContents().add(owner);
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
        URI pending = service.publicURI(target).appendFragment(ROOT_FRAGMENT);
        ((InternalEObject) proxy).eSetProxyURI(pending);
        annotation.getContents().add(proxy);
        sourceResource.getContents().add(annotation);
        resources.getResources().addAll(List.of(sourceResource, targetResource));

        service.reconcile(resources, List.of(source, target));
        assertThat(((InternalEObject) proxy).eProxyURI()).isEqualTo(pending);
        service.reconcile(resources, List.of(new ResourceDocument(source.id(), SOURCE_PATH, false), target));
        assertThat(proxy.eIsProxy()).isTrue();
        assertThat(((InternalEObject) proxy).eProxyURI()).isEqualTo(targetResource.getURI().appendFragment(ROOT_FRAGMENT));
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
        for (URI uri : List.of(external.appendFragment(ROOT_FRAGMENT), URI.createURI(EcorePackage.eNS_URI).appendFragment("//EClass"))) {
            var resource = new ResourceImpl(service.publicURI(document));
            var proxy = EcoreFactory.eINSTANCE.createEClass();
            ((InternalEObject) proxy).eSetProxyURI(uri);
            var owner = EcoreFactory.eINSTANCE.createEClass();
            owner.getESuperTypes().add(proxy);
            resource.getContents().add(owner);
            service.canonicalize(resource, document, List.of(document), resources);
            assertThat(((InternalEObject) proxy).eProxyURI()).isEqualTo(uri);
        }
        assertThat(resources.getResources()).containsExactly(loaded);
    }

    @Test
    public void reconciliationDoesNotMutateResourcesOutsideTheDocumentSet() {
        var target = new ResourceDocument(UUID.randomUUID(), TARGET_PATH, false);
        var service = new ResourceReferences();
        var resources = new DetachedResourceSet();
        var factory = new JSONResourceFactory();
        var targetResource = factory.createResource(factory.createResourceURI(target.id().toString()));
        targetResource.getContents().add(EcoreFactory.eINSTANCE.createEClass());
        var external = new ResourceImpl(URI.createURI("urn:shared-metamodel"));
        var owner = EcoreFactory.eINSTANCE.createEClass();
        var proxy = EcoreFactory.eINSTANCE.createEClass();
        URI pending = service.publicURI(target).appendFragment(ROOT_FRAGMENT);
        ((InternalEObject) proxy).eSetProxyURI(pending);
        owner.getESuperTypes().add(proxy);
        external.getContents().add(owner);
        resources.getResources().addAll(List.of(external, targetResource));

        service.reconcile(resources, List.of(target));

        assertThat(((InternalEObject) proxy).eProxyURI()).isEqualTo(pending);
    }

    @Test
    public void referenceFragmentsCannotNavigateThroughOtherReferences() {
        var source = new ResourceDocument(UUID.randomUUID(), SOURCE_PATH, false);
        var target = new ResourceDocument(UUID.randomUUID(), TARGET_PATH, false);
        var service = new ResourceReferences();
        var resources = new DetachedResourceSet();
        var factory = new JSONResourceFactory();
        var targetResource = factory.createResource(factory.createResourceURI(target.id().toString()));
        var targetObject = EcoreFactory.eINSTANCE.createEClass();
        var external = EcoreFactory.eINSTANCE.createEClass();
        ((InternalEObject) external).eSetProxyURI(URI.createURI("https://example.invalid/model.ecore#/"));
        targetObject.getESuperTypes().add(external);
        targetResource.getContents().add(targetObject);
        resources.getResources().add(targetResource);
        var sourceResource = new ResourceImpl(service.publicURI(source));
        var owner = EcoreFactory.eINSTANCE.createEClass();
        var proxy = EcoreFactory.eINSTANCE.createEClass();
        URI pending = service.publicURI(target).appendFragment("//@eSuperTypes.0");
        ((InternalEObject) proxy).eSetProxyURI(pending);
        owner.getESuperTypes().add(proxy);
        sourceResource.getContents().add(owner);

        service.canonicalize(sourceResource, source, List.of(source, target), resources);

        assertThat(((InternalEObject) proxy).eProxyURI()).isEqualTo(pending);
        assertThat(external.eIsProxy()).isTrue();
        assertThat(resources.getResources()).containsExactly(targetResource);
    }

    @Test
    public void fragmentsAnchoredAtAnOriginalIdResolveToOwnedDescendants() {
        var source = new ResourceDocument(UUID.randomUUID(), SOURCE_PATH, false);
        var target = new ResourceDocument(UUID.randomUUID(), TARGET_PATH, false);
        var service = new ResourceReferences();
        var resources = new DetachedResourceSet();
        var factory = new JSONResourceFactory();
        var targetResource = factory.createResource(factory.createResourceURI(target.id().toString()));
        var model = EcoreFactory.eINSTANCE.createEPackage();
        var type = EcoreFactory.eINSTANCE.createEClass();
        type.setName("Target");
        model.getEClassifiers().add(type);
        targetResource.getContents().add(model);
        targetResource.setID(model, service.objectId(target.id(), "original-package-id"));
        resources.getResources().add(targetResource);
        var sourceResource = new ResourceImpl(service.publicURI(source));
        var owner = EcoreFactory.eINSTANCE.createEClass();
        var proxy = EcoreFactory.eINSTANCE.createEClass();
        ((InternalEObject) proxy).eSetProxyURI(service.publicURI(target).appendFragment("/?original-package-id/Target"));
        owner.getESuperTypes().add(proxy);
        sourceResource.getContents().add(owner);

        service.canonicalize(sourceResource, source, List.of(source, target), resources);

        assertThat(((InternalEObject) proxy).eProxyURI()).isEqualTo(targetResource.getURI().appendFragment(targetResource.getURIFragment(type)));
    }
}
