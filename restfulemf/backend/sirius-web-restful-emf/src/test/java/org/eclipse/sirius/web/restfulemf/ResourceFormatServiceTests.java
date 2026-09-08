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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.sirius.components.emf.services.EObjectIDManager;
import org.eclipse.sirius.emfjson.resource.JsonResource;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;
import org.eclipse.sirius.web.restfulemf.configuration.RestfulEMFProperties;
import org.eclipse.sirius.web.restfulemf.services.DetachedResourceSet;
import org.eclipse.sirius.web.restfulemf.services.ResourceFormatService;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;
import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;

/**
 * Exercises real XMI, binary and JSON conversion, including durable forward references.
 */
public class ResourceFormatServiceTests {

    private static final String ANNOTATION = "<ecore:EClass xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\" xmlns:xmi=\"http://www.omg.org/XMI\" ";

    private static final String TARGET_ID = "4f5b9d8c-61b3-46dd-9bf5-e790b2cb5dd7";

    private static final String TARGET_PATH = "common/target.ecore";

    private static final String NODE = "<model:Node xmlns:model=\"urn:restful:test\" ";

    @Test
    public void givenMultipleMetamodelsWhenExportedAsXmiThenBothCanBeLoaded() throws IOException {
        var service = new ResourceFormatService(this::snapshot, List.of(EcorePackage.eINSTANCE, this.model()),
                new RestfulEMFProperties(false, DataSize.ofMegabytes(1), DataSize.ofMegabytes(1), 2));
        var output = new ByteArrayOutputStream();
        service.serializeEPackages("project", ResourceFormat.XMI, output);
        var downloaded = new XMIResourceImpl();
        downloaded.load(new ByteArrayInputStream(output.toByteArray()), Map.of());
        assertThat(downloaded.getContents()).hasSize(2).allMatch(EPackage.class::isInstance);
    }

    @Test
    public void givenDuplicateObjectIdsWhenImportedThenTheDocumentIsRejected() {
        String root = ANNOTATION + "xmi:id=\"" + TARGET_ID + "\"/>";
        String xmi = "<xmi:XMI xmlns:xmi=\"http://www.omg.org/XMI\">" + root + root + "</xmi:XMI>";
        var document = new ResourceDocument(UUID.randomUUID(), TARGET_PATH, false);
        assertThatThrownBy(() -> this.service().deserialize(this.content(xmi), document, ResourceFormat.XMI))
                .isInstanceOf(org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException.class);
    }

    @Test
    public void givenIntrinsicIdentifiersAndCycleWhenImportedThenAttributesRemainUnchanged() throws Exception {
        EPackage model = this.model();
        var service = new ResourceFormatService(this::snapshot, List.of(EcorePackage.eINSTANCE, model),
                new RestfulEMFProperties(false, DataSize.ofMegabytes(1), DataSize.ofMegabytes(1), 2));
        var source = new ResourceDocument(UUID.randomUUID(), "domain/source.ecore", false);
        var target = new ResourceDocument(UUID.randomUUID(), TARGET_PATH, false);
        var documents = List.of(source, target);
        var resources = this.resourceSet();
        resources.getPackageRegistry().put(model.getNsURI(), model);
        Resource first = this.load(service.deserialize(this.content(NODE + "id=\"first\" link=\"../common/target.ecore#second\"/>"),
                source, ResourceFormat.XMI, documents, resources), source, resources);
        Resource second = this.load(service.deserialize(this.content(NODE + "id=\"second\" link=\"../domain/source.ecore#first\"/>"),
                target, ResourceFormat.XMI, documents, resources), target, resources);
        service.reconcileReferences(resources, documents);
        var firstObject = first.getContents().getFirst();
        var secondObject = second.getContents().getFirst();
        var node = (EClass) model.getEClassifier("Node");
        assertThat(firstObject.eGet(node.getEStructuralFeature("id"))).isEqualTo("first");
        assertThat(secondObject.eGet(node.getEStructuralFeature("id"))).isEqualTo("second");
        assertThat(firstObject.eGet(node.getEStructuralFeature("link"))).isSameAs(secondObject);
        assertThat(secondObject.eGet(node.getEStructuralFeature("link"))).isSameAs(firstObject);
    }

    @Test
    public void givenForwardReferenceWhenTargetArrivesAfterRestartThenOriginalFragmentResolves() throws Exception {
        for (String targetId : List.of("original-id", TARGET_ID)) {
            var source = new ResourceDocument(UUID.randomUUID(), "domain/source.ecore", false);
            var target = new ResourceDocument(UUID.randomUUID(), TARGET_PATH, false);
            var documents = List.of(source, target);
            var service = this.service();
            var resources = this.resourceSet();
            String sourceXMI = ANNOTATION + "name=\"source\"><eSuperTypes href=\"../common/target.ecore#" + targetId + "\"/></ecore:EClass>";
            ResourceSnapshot imported = service.deserialize(this.content(sourceXMI), source, ResourceFormat.XMI, documents, resources);
            assertThat(imported.content()).contains("restfulemf:/documents/common/target.ecore");
            Resource referencing = this.load(imported, source, resources);
            String targetXMI = ANNOTATION + "xmi:id=\"" + targetId + "\" name=\"target\"/>";
            ResourceSnapshot targetSnapshot = service.deserialize(this.content(targetXMI), target, ResourceFormat.XMI, documents, resources);
            Resource targetResource = this.load(targetSnapshot, target, resources);

            service.reconcileReferences(resources, documents);

            var annotation = (EClass) referencing.getContents().getFirst();
            assertThat(annotation.getESuperTypes().getFirst()).isSameAs(targetResource.getContents().getFirst());
            var output = new ByteArrayOutputStream();
            service.serialize(this.snapshot(referencing).orElseThrow(), source, documents, ResourceFormat.XMI, "\t", output);
            assertThat(output.toString(StandardCharsets.UTF_8)).contains("../common/target.ecore#").doesNotContain("sirius:", "restfulemf:");
        }
    }

    @Test
    public void givenStructuralReferenceToExplicitUUIDWhenImportedThenTargetUUIDWinsOverDerivedId() throws Exception {
        var source = new ResourceDocument(UUID.randomUUID(), "source.ecore", false);
        var target = new ResourceDocument(UUID.randomUUID(), TARGET_PATH, false);
        var documents = List.of(source, target);
        var service = this.service();
        var resources = this.resourceSet();
        this.load(service.deserialize(this.content(ANNOTATION + "xmi:id=\"" + TARGET_ID + "\"/>"), target, ResourceFormat.XMI), target, resources);
        String xmi = ANNOTATION + "><eSuperTypes href=\"common/target.ecore#/\"/></ecore:EClass>";

        Resource imported = this.load(service.deserialize(this.content(xmi), source, ResourceFormat.XMI, documents, resources), source, resources);

        var annotation = (EClass) imported.getContents().getFirst();
        var proxy = (InternalEObject) ((org.eclipse.emf.ecore.util.InternalEList<?>) annotation.getESuperTypes()).basicGet(0);
        assertThat(proxy.eProxyURI().fragment()).isEqualTo(TARGET_ID);
    }

    @Test
    public void givenSameInputWhenReimportedThenIdentifiersAndRepresentationDigestsAreStable() throws Exception {
        var document = new ResourceDocument(UUID.randomUUID(), "école/model space.ecore", false);
        var service = this.service();
        String xmi = ANNOTATION + "xmi:id=\"not-a-uuid\" name=\"value\"/>";
        ResourceSnapshot first = service.deserialize(this.content(xmi), document, ResourceFormat.XMI);
        ResourceSnapshot second = service.deserialize(this.content(xmi), document, ResourceFormat.XMI);
        assertThat(second.content()).isEqualTo(first.content());
        for (ResourceFormat format : List.of(ResourceFormat.XMI, ResourceFormat.BINARY, ResourceFormat.ZIPPED_XMI)) {
            var output = new ByteArrayOutputStream();
            service.serialize(first, document, format, "\t", output);
            String digest = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(output.toByteArray()));
            assertThat(service.representationRevision(first, document, List.of(document), format, "\t")).isEqualTo(digest);
            ResourceSnapshot roundTrip = service.deserialize(new ByteArrayInputStream(output.toByteArray()), document, format);
            assertThat(roundTrip.content()).isEqualTo(first.content());
        }
    }

    @Test
    public void annotationsAndOperationsHaveRepeatableBinaryRepresentations() {
        var document = new ResourceDocument(UUID.randomUUID(), "operations.ecore", false);
        var service = this.service();
        String xmi = ANNOTATION + "name=\"owner\"><eAnnotations source=\"test\"><details key=\"a\" value=\"b\"/></eAnnotations>"
                + "<eOperations name=\"run\"><eParameters name=\"arg\"/></eOperations></ecore:EClass>";
        var snapshot = service.deserialize(this.content(xmi), document, ResourceFormat.XMI);
        var first = new ByteArrayOutputStream();
        var second = new ByteArrayOutputStream();
        service.serialize(snapshot, document, ResourceFormat.BINARY, "\t", first);
        service.serialize(snapshot, document, ResourceFormat.BINARY, "\t", second);
        assertThat(second.toByteArray()).isEqualTo(first.toByteArray());
    }

    @Test
    public void upstreamJsonDoesNotRestoreExplicitAnnotationIdentifiers() throws IOException {
        var document = new ResourceDocument(UUID.randomUUID(), "annotation-reference.ecore", false);
        var factory = new JSONResourceFactory();
        var resource = factory.createResource(factory.createResourceURI(document.id().toString()));
        var owner = EcoreFactory.eINSTANCE.createEClass();
        var target = EcoreFactory.eINSTANCE.createEAnnotation();
        target.setSource("target");
        var source = EcoreFactory.eINSTANCE.createEAnnotation();
        source.setSource("source");
        owner.getEAnnotations().addAll(List.of(target, source));
        source.getReferences().add(target);
        resource.getContents().add(owner);
        new EObjectIDManager().setId(target, TARGET_ID);
        var snapshot = this.snapshot(resource).orElseThrow();
        var restored = this.load(snapshot, document, this.resourceSet());
        // Documents the upstream limitation rather than mistaking reproducible ETags for restored identity.
        assertThat(snapshot.content()).contains(TARGET_ID);
        assertThat(restored.getEObject(TARGET_ID)).isNull();
        assertThat(((EClass) restored.getContents().getFirst()).getEAnnotations().get(1).getReferences()).isEmpty();
    }

    @Test
    public void givenEscapingReferenceWhenImportedThenItIsRejectedWithoutLoadingExternalResources() {
        var service = this.service();
        var document = new ResourceDocument(UUID.randomUUID(), "source.ecore", false);
        String xmi = ANNOTATION + "><eSuperTypes href=\"../../outside.ecore#/\"/></ecore:EClass>";
        assertThatThrownBy(() -> service.deserialize(this.content(xmi), document, ResourceFormat.XMI))
                .hasMessageContaining("escapes");
        assertThat(this.resourceSet().getResource(URI.createURI("https://example.invalid/model.ecore"), true)).isNull();
    }

    private ResourceFormatService service() {
        return new ResourceFormatService(this::snapshot, List.of(EcorePackage.eINSTANCE),
                new RestfulEMFProperties(false, DataSize.ofMegabytes(1), DataSize.ofMegabytes(1), 2));
    }

    private EPackage model() {
        var model = EcoreFactory.eINSTANCE.createEPackage();
        model.setName("model");
        model.setNsPrefix("model");
        model.setNsURI("urn:restful:test");
        var node = EcoreFactory.eINSTANCE.createEClass();
        node.setName("Node");
        model.getEClassifiers().add(node);
        var id = EcoreFactory.eINSTANCE.createEAttribute();
        id.setName("id");
        id.setID(true);
        id.setEType(EcorePackage.eINSTANCE.getEString());
        node.getEStructuralFeatures().add(id);
        var link = EcoreFactory.eINSTANCE.createEReference();
        link.setName("link");
        link.setEType(node);
        node.getEStructuralFeatures().add(link);
        return model;
    }

    private Optional<ResourceSnapshot> snapshot(Resource resource) {
        var output = new ByteArrayOutputStream();
        try {
            resource.save(output, Map.of(JsonResource.OPTION_ID_MANAGER, new EObjectIDManager(), JsonResource.OPTION_SCHEMA_LOCATION, true));
            return Optional.of(new ResourceSnapshot(output.toString(StandardCharsets.UTF_8), "test-revision"));
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private Resource load(ResourceSnapshot snapshot, ResourceDocument document, ResourceSet resources) throws IOException {
        Resource resource = new JSONResourceFactory().createResource(new JSONResourceFactory().createResourceURI(document.id().toString()));
        resources.getResources().add(resource);
        resource.load(this.content(snapshot.content()), Map.of());
        return resource;
    }

    private ResourceSet resourceSet() {
        var resources = new DetachedResourceSet();
        resources.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        return resources;
    }

    private ByteArrayInputStream content(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }
}
