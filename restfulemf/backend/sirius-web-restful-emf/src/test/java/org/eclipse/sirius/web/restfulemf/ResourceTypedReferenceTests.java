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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.components.emf.services.EObjectIDManager;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.emfjson.resource.JsonResource;
import org.eclipse.sirius.emfjson.resource.PackageNotFoundError;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;
import org.eclipse.sirius.web.restfulemf.configuration.RestfulEMFProperties;
import org.eclipse.sirius.web.restfulemf.services.DetachedResourceSet;
import org.eclipse.sirius.web.restfulemf.services.ResourceFormatService;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;
import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;

/**
 * Reproduces emfjson's misleading missing-package diagnostic for a valid pending typed reference.
 *
 * @author cbrun
 */
public class ResourceTypedReferenceTests {

    @Test
    public void pendingETypeSurvivesJsonReloadAndResolvesWhenTheTargetArrives() throws IOException {
        var documents = List.of(new ResourceDocument(UUID.randomUUID(), "domain/first.ecore", false),
                new ResourceDocument(UUID.randomUUID(), "common/second.ecore", false));
        var service = new ResourceFormatService(this::snapshot, List.of(EcorePackage.eINSTANCE),
                new RestfulEMFProperties(false, DataSize.ofMegabytes(1), DataSize.ofMegabytes(1), 2));
        var resources = new DetachedResourceSet();
        resources.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        String header = "<ecore:EPackage xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"p\" nsPrefix=\"p\" nsURI=\"urn:p\">";
        String first = header + "<eClassifiers xsi:type=\"ecore:EClass\" name=\"A\"><eStructuralFeatures xsi:type=\"ecore:EReference\" name=\"other\">"
                + "<eType xsi:type=\"ecore:EClass\" href=\"../common/second.ecore#//B\"/></eStructuralFeatures></eClassifiers></ecore:EPackage>";
        var snapshot = service.deserialize(this.content(first), documents.getFirst(), ResourceFormat.XMI, documents, resources);
        var factory = new JSONResourceFactory();
        Resource source = factory.createResource(factory.createResourceURI(documents.getFirst().id().toString()));
        resources.getResources().add(source);
        source.load(this.content(snapshot.content()), Map.of());
        var owner = (EClass) ((EPackage) source.getContents().getFirst()).getEClassifiers().getFirst();
        var reference = owner.getEStructuralFeatures().getFirst();
        var pending = (InternalEObject) reference.eGet(EcorePackage.Literals.ETYPED_ELEMENT__ETYPE, false);
        assertThat(pending.eIsProxy()).isTrue();
        assertThat(pending.eProxyURI().toString()).isEqualTo("restfulemf:/documents/common/second.ecore#//B");
        assertThat(source.getErrors()).allMatch(PackageNotFoundError.class::isInstance);

        String second = header + "<eClassifiers xsi:type=\"ecore:EClass\" name=\"B\"/></ecore:EPackage>";
        var targetSnapshot = service.deserialize(this.content(second), documents.getLast(), ResourceFormat.XMI, documents, resources);
        Resource target = factory.createResource(factory.createResourceURI(documents.getLast().id().toString()));
        resources.getResources().add(target);
        target.load(this.content(targetSnapshot.content()), Map.of());
        service.reconcileReferences(resources, documents);
        assertThat(reference.getEType()).isSameAs(((EPackage) target.getContents().getFirst()).getEClassifiers().getFirst());
    }

    private ByteArrayInputStream content(String text) {
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
    }

    private Optional<ResourceSnapshot> snapshot(Resource resource) {
        try {
            var output = new ByteArrayOutputStream();
            resource.save(output, Map.of(JsonResource.OPTION_ID_MANAGER, new EObjectIDManager(), JsonResource.OPTION_SCHEMA_LOCATION, true));
            return Optional.of(new ResourceSnapshot(output.toString(StandardCharsets.UTF_8), "revision"));
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
