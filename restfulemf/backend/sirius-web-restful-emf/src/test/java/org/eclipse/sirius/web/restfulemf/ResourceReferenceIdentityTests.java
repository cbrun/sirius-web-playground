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
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.services.DetachedResourceSet;
import org.eclipse.sirius.web.restfulemf.services.ResourceReferences;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;
import org.junit.jupiter.api.Test;

/**
 * Prevents accepting references whose target identifiers the upstream JSON format would discard.
 *
 * @author cbrun
 */
public class ResourceReferenceIdentityTests {

    private static final String SOURCE_PATH = "source.ecore";

    @Test
    public void intraDocumentAnnotationReferenceIsRejectedBeforePersistence() {
        var document = new ResourceDocument(UUID.randomUUID(), SOURCE_PATH, false);
        var service = new ResourceReferences();
        var resource = new ResourceImpl(service.publicURI(document));
        var owner = EcoreFactory.eINSTANCE.createEClass();
        var source = EcoreFactory.eINSTANCE.createEAnnotation();
        var target = EcoreFactory.eINSTANCE.createEAnnotation();
        owner.getEAnnotations().addAll(List.of(source, target));
        source.getReferences().add(target);
        resource.getContents().add(owner);

        assertThatThrownBy(() -> service.canonicalize(resource, document, List.of(document), new DetachedResourceSet()))
                .isInstanceOf(RestfulEMFException.class).hasMessageContaining("EAnnotation");
    }

    @Test
    public void arrivingAnnotationTargetDoesNotConvertPendingReferenceToAnUnstableUUID() {
        var source = new ResourceDocument(UUID.randomUUID(), SOURCE_PATH, false);
        var target = new ResourceDocument(UUID.randomUUID(), "target.ecore", false);
        var resources = new DetachedResourceSet();
        var factory = new JSONResourceFactory();
        var sourceResource = factory.createResource(factory.createResourceURI(source.id().toString()));
        var targetResource = factory.createResource(factory.createResourceURI(target.id().toString()));
        var targetObject = EcoreFactory.eINSTANCE.createEAnnotation();
        targetResource.getContents().add(targetObject);
        String targetId = UUID.randomUUID().toString();
        targetResource.setID(targetObject, targetId);
        var referencing = EcoreFactory.eINSTANCE.createEAnnotation();
        var proxy = EcoreFactory.eINSTANCE.createEClass();
        var service = new ResourceReferences();
        URI pending = service.publicURI(target).appendFragment(targetId);
        ((InternalEObject) proxy).eSetProxyURI(pending);
        referencing.getReferences().add(proxy);
        sourceResource.getContents().add(referencing);
        resources.getResources().addAll(List.of(sourceResource, targetResource));

        assertThatThrownBy(() -> service.reconcile(resources, List.of(source, target))).isInstanceOf(RestfulEMFException.class);
        assertThat(((InternalEObject) proxy).eProxyURI()).isEqualTo(pending);
    }

    @Test
    public void referencesToRegisteredExternalMetamodelOperationsRemainAllowed() {
        var document = new ResourceDocument(UUID.randomUUID(), SOURCE_PATH, false);
        var resources = new DetachedResourceSet();
        var metamodel = EcoreFactory.eINSTANCE.createEPackage();
        metamodel.setNsURI("urn:registered-metamodel");
        resources.getPackageRegistry().put(metamodel.getNsURI(), metamodel);
        var external = new ResourceImpl(URI.createURI(metamodel.getNsURI()));
        external.getContents().add(metamodel);
        var type = EcoreFactory.eINSTANCE.createEClass();
        metamodel.getEClassifiers().add(type);
        var operation = EcoreFactory.eINSTANCE.createEOperation();
        type.getEOperations().add(operation);
        var service = new ResourceReferences();
        var resource = new ResourceImpl(service.publicURI(document));
        var source = EcoreFactory.eINSTANCE.createEAnnotation();
        source.getReferences().add(operation);
        resource.getContents().add(source);

        service.canonicalize(resource, document, List.of(document), resources);
        assertThat(source.getReferences()).containsExactly(operation);
    }
}
