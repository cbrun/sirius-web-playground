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
package org.eclipse.sirius.web.restfulemf.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.sirius.components.collaborative.api.Monitoring;
import org.eclipse.sirius.components.core.api.IEditingContextSearchService;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.graphql.api.IEditingContextDispatcher;
import org.eclipse.sirius.web.application.project.services.api.IProjectEditingContextService;
import org.eclipse.sirius.web.tests.data.GivenSiriusWebServer;
import org.eclipse.sirius.web.tests.services.api.IGivenInitialServerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import fr.obeo.dsl.designer.sample.flow.FlowPackage;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.ReplaceDocumentEventHandler;
import org.eclipse.sirius.web.restfulemf.ReplaceResourceContentInput;
import org.eclipse.sirius.web.restfulemf.ReplaceResourceContentSuccessPayload;
import org.eclipse.sirius.web.restfulemf.RestfulEMFURIHandler;
import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Sinks;

/**
 * Integration tests of the RESTful EMF write endpoints.
 */
@GivenSiriusWebServer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "sirius.web.restful-emf.max-request-size=1MB",
        "sirius.web.restful-emf.max-uncompressed-size=1MB"
})
public class RestfulEMFWriteIntegrationTests extends AbstractIntegrationTests {

    private static final String FLOW_PROJECT_ID = "d419bbee-9cba-4b85-972c-660d875ad705";

    private static final String FLOW_DOCUMENT_ID = "96d6cae1-da84-40d8-94c3-f4af14462485";

    private static final String FLOW_XMI_URI = "/api/rest/projects/" + FLOW_PROJECT_ID + "/documents/xmi/Flow";

    private static final Pattern XMI_ID_PATTERN = Pattern.compile("xmi:id=\"([^\"]+)\"");

    @LocalServerPort
    private int port;

    @Autowired
    private IGivenInitialServerState givenInitialServerState;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private IProjectEditingContextService projectEditingContextService;

    @Autowired
    private IEditingContextSearchService editingContextSearchService;

    @Autowired
    private IEditingContextDispatcher editingContextDispatcher;

    @Autowired
    private ReplaceDocumentEventHandler replaceDocumentEventHandler;

    @Autowired
    private MeterRegistry meterRegistry;

    private WebTestClient webTestClient;

    @BeforeEach
    public void beforeEach() {
        this.givenInitialServerState.initialize();
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port).build();
    }

    @Test
    @DisplayName("Given a Flow document, when updated as XMI, then the change is persisted with its identifiers")
    public void givenFlowDocumentWhenUpdatedAsXMIThenTheChangeIsPersistedWithItsIdentifiers() {
        var initialResult = this.get(FLOW_XMI_URI);
        byte[] initialXMI = initialResult.getResponseBody();
        String initialRevision = initialResult.getResponseHeaders().getETag();
        String updatedXMI = this.renameFirstElement(initialXMI, "CompositeProcessorRenamed");
        List<String> initialIds = this.getIds(updatedXMI);

        this.put(FLOW_XMI_URI, updatedXMI.getBytes(StandardCharsets.UTF_8), initialRevision)
                .expectStatus().isNoContent()
                .expectHeader().doesNotExist("ETag")
                .expectBody().isEmpty();
        assertThat(initialRevision).isNotBlank();
        String persistedXMI = new String(this.getBytes(FLOW_XMI_URI), StandardCharsets.UTF_8);
        assertThat(persistedXMI).contains("CompositeProcessorRenamed");
        assertThat(this.getIds(persistedXMI)).containsExactlyElementsOf(initialIds);

        this.givenInitialServerState.initialize();
        assertThat(new String(this.getBytes(FLOW_XMI_URI), StandardCharsets.UTF_8)).contains("CompositeProcessorRenamed");
    }

    @Test
    @DisplayName("Given two revisions of a Flow document, when the stale revision is saved, then the concurrent update is preserved")
    public void givenTwoRevisionsOfAFlowDocumentWhenTheStaleRevisionIsSavedThenTheConcurrentUpdateIsPreserved() {
        var initialResult = this.get(FLOW_XMI_URI);
        byte[] initialXMI = initialResult.getResponseBody();
        String initialRevision = initialResult.getResponseHeaders().getETag();
        byte[] firstUpdate = this.renameFirstElement(initialXMI, "FirstUpdate").getBytes(StandardCharsets.UTF_8);
        byte[] staleUpdate = this.renameFirstElement(initialXMI, "StaleUpdate").getBytes(StandardCharsets.UTF_8);

        this.put(FLOW_XMI_URI, firstUpdate, initialRevision)
                .expectStatus().isNoContent()
                .expectHeader().doesNotExist("ETag")
                .expectBody().isEmpty();
        this.put(FLOW_XMI_URI, staleUpdate, initialRevision)
                .expectStatus().isEqualTo(412)
                .expectBody()
                .jsonPath("$.code").isEqualTo("REVISION_CONFLICT");

        assertThat(new String(this.getBytes(FLOW_XMI_URI), StandardCharsets.UTF_8)).contains("FirstUpdate").doesNotContain("StaleUpdate");
    }

    @Test
    @DisplayName("Given two EMF clients, when both save the same Flow document, then the stale Resource save fails")
    public void givenTwoEMFClientsWhenBothSaveTheSameFlowDocumentThenTheStaleResourceSaveFails() throws IOException {
        URI uri = URI.createURI("http://localhost:" + this.port + FLOW_XMI_URI.replace("/xmi", "/bin"));
        Resource firstClientResource = this.loadBinaryResource(uri);
        Resource secondClientResource = this.loadBinaryResource(uri);
        EObject firstClientObject = this.getNamedObject(firstClientResource);
        EObject secondClientObject = this.getNamedObject(secondClientResource);
        EStructuralFeature firstClientName = firstClientObject.eClass().getEStructuralFeature("name");
        EStructuralFeature secondClientName = secondClientObject.eClass().getEStructuralFeature("name");

        secondClientObject.eSet(secondClientName, secondClientObject.eGet(secondClientName) + "-second-client");
        secondClientResource.save(this.getBinaryOptions());
        firstClientObject.eSet(firstClientName, firstClientObject.eGet(firstClientName) + "-first-client");

        assertThatThrownBy(() -> firstClientResource.save(this.getBinaryOptions()))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("412");
    }

    @Test
    @DisplayName("Given a cross-resource reference, when its target document is replaced, then the reference resolves to the new object")
    public void givenCrossResourceReferenceWhenItsTargetDocumentIsReplacedThenTheReferenceResolvesToTheNewObject() throws IOException {
        var initialResult = this.get(FLOW_XMI_URI);
        IEMFEditingContext editingContext = this.getEditingContext();
        Resource targetResource = editingContext.getDomain().getResourceSet().getResources().stream()
                .filter(resource -> FLOW_DOCUMENT_ID.equals(resource.getURI().lastSegment()))
                .findFirst()
                .orElseThrow();
        EObject previousTarget = this.getNamedObject(targetResource);
        Resource referencingResource = new JSONResourceFactory().createResourceFromPath(UUID.randomUUID().toString());
        referencingResource.eAdapters().add(new ResourceMetadataAdapter("references.ecore"));
        var annotation = EcoreFactory.eINSTANCE.createEAnnotation();
        annotation.getReferences().add(previousTarget);
        referencingResource.getContents().add(annotation);
        editingContext.getDomain().getResourceSet().getResources().add(referencingResource);
        assertThat(EcoreUtil.UsageCrossReferencer.find(previousTarget, editingContext.getDomain().getResourceSet())).isNotEmpty();

        try {
            byte[] updatedXMI = this.renameFirstElement(initialResult.getResponseBody(), "CrossResourceTarget").getBytes(StandardCharsets.UTF_8);
            var input = new ReplaceResourceContentInput(UUID.randomUUID(), "Flow", ResourceFormat.XMI, updatedXMI, List.of(), false);
            var payloadSink = Sinks.<IPayload>one();
            var counter = this.meterRegistry.get(Monitoring.EVENT_HANDLER)
                    .tag(Monitoring.NAME, ReplaceDocumentEventHandler.class.getSimpleName())
                    .counter();
            double initialCount = counter.count();
            this.replaceDocumentEventHandler.handle(payloadSink, Sinks.many().unicast().onBackpressureBuffer(), editingContext, input);
            assertThat(payloadSink.asMono().block()).isInstanceOf(ReplaceResourceContentSuccessPayload.class);
            assertThat(counter.count()).isEqualTo(initialCount + 1);

            EObject resolvedTarget = annotation.getReferences().get(0);
            assertThat(resolvedTarget).isNotSameAs(previousTarget);
            assertThat(resolvedTarget.eResource()).isSameAs(targetResource);
            assertThat(resolvedTarget.eGet(resolvedTarget.eClass().getEStructuralFeature("name"))).isEqualTo("CrossResourceTarget");
        } finally {
            editingContext.getDomain().getResourceSet().getResources().remove(referencingResource);
        }
    }

    @Test
    @DisplayName("Given invalid model content, when replacement fails, then the previous document is preserved")
    public void givenInvalidModelContentWhenReplacementFailsThenThePreviousDocumentIsPreserved() {
        String initialXMI = new String(this.getBytes(FLOW_XMI_URI), StandardCharsets.UTF_8);
        String invalidContent = initialXMI.replace(FlowPackage.eNS_URI, "urn:unknown:flow");
        assertThat(invalidContent).isNotEqualTo(initialXMI);
        String editingContextId = this.projectEditingContextService.getEditingContextId(FLOW_PROJECT_ID).orElseThrow();
        var input = new ReplaceResourceContentInput(UUID.randomUUID(), "Flow", ResourceFormat.XMI,
                invalidContent.getBytes(StandardCharsets.UTF_8), List.of(), false);

        assertThatThrownBy(() -> this.editingContextDispatcher.dispatchMutation(editingContextId, input).block(Duration.ofSeconds(10)))
                .isInstanceOf(RestfulEMFException.class);
        assertThat(new String(this.getBytes(FLOW_XMI_URI), StandardCharsets.UTF_8)).isEqualTo(initialXMI);
    }

    @Test
    @DisplayName("Given a Flow document, when its zipped XMI and binary representations are written back, then both are accepted")
    public void givenFlowDocumentWhenItsZippedXMIAndBinaryRepresentationsAreWrittenBackThenBothAreAccepted() {
        String zippedURI = "/api/rest/projects/" + FLOW_PROJECT_ID + "/documents/xmi.zip/Flow";
        String binaryURI = "/api/rest/projects/" + FLOW_PROJECT_ID + "/documents/bin/Flow";

        this.put(zippedURI, this.getBytes(zippedURI)).expectStatus().isNoContent();
        this.put(binaryURI, this.getBytes(binaryURI)).expectStatus().isNoContent();

        assertThat(this.getBytes(zippedURI)).isNotEmpty();
        assertThat(this.getBytes(binaryURI)).isNotEmpty();
    }

    @Test
    @DisplayName("Given invalid EMF input, when it is uploaded, then bad request is returned")
    public void givenInvalidEMFInputWhenItIsUploadedThenBadRequestIsReturned() {
        byte[] unsafeXML = ("<?xml version=\"1.0\"?><!DOCTYPE xmi:XMI [<!ENTITY xxe SYSTEM \"file:///does-not-exist\">]>"
                + "<xmi:XMI xmlns:xmi=\"http://www.omg.org/XMI\">&xxe;</xmi:XMI>").getBytes(StandardCharsets.UTF_8);

        this.put(FLOW_XMI_URI, unsafeXML)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_RESOURCE");
        this.put("/api/rest/projects/" + FLOW_PROJECT_ID + "/documents/bin/Flow", new byte[] { 1, 2, 3 }).expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Given oversized raw or compressed content, when it is uploaded, then payload too large is returned")
    public void givenOversizedRawOrCompressedContentWhenItIsUploadedThenPayloadTooLargeIsReturned() throws IOException {
        this.put(FLOW_XMI_URI, new byte[1024 * 1024 + 1])
                .expectStatus().isEqualTo(413)
                .expectBody()
                .jsonPath("$.code").isEqualTo("PAYLOAD_TOO_LARGE");

        var compressedContent = new ByteArrayOutputStream();
        try (var zipOutputStream = new ZipOutputStream(compressedContent)) {
            zipOutputStream.putNextEntry(new ZipEntry("model.xmi"));
            zipOutputStream.write(("<?xml version=\"1.0\"?><xmi:XMI xmlns:xmi=\"http://www.omg.org/XMI\">"
                    + " ".repeat(1024 * 1024) + "</xmi:XMI>").getBytes(StandardCharsets.UTF_8));
        }
        this.put("/api/rest/projects/" + FLOW_PROJECT_ID + "/documents/xmi.zip/Flow", compressedContent.toByteArray())
                .expectStatus().isEqualTo(413)
                .expectBody()
                .jsonPath("$.code").isEqualTo("PAYLOAD_TOO_LARGE");
    }

    @Test
    @DisplayName("Given a read-only Flow document, when an update is requested, then forbidden is returned")
    public void givenReadOnlyFlowDocumentWhenAnUpdateIsRequestedThenForbiddenIsReturned() {
        byte[] xmi = this.getBytes(FLOW_XMI_URI);
        this.givenInitialServerState.initialize();
        this.jdbcTemplate.update("UPDATE document SET is_read_only = true WHERE id = ?::uuid", FLOW_DOCUMENT_ID);

        this.put(FLOW_XMI_URI, xmi)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.code").isEqualTo("READ_ONLY");
    }

    @Test
    @DisplayName("Given CSV output, when a CSV update is requested, then method not allowed is returned")
    public void givenCSVOutputWhenACSVUpdateIsRequestedThenMethodNotAllowedIsReturned() {
        this.put("/api/rest/projects/" + FLOW_PROJECT_ID + "/documents/csv/Flow", "id".getBytes(StandardCharsets.UTF_8))
                .expectStatus().isEqualTo(405)
                .expectHeader().valueEquals(HttpHeaders.ALLOW, "GET,HEAD")
                .expectBody()
                .jsonPath("$.code").isEqualTo("METHOD_NOT_ALLOWED");
    }

    private byte[] getBytes(String uri) {
        return this.get(uri).getResponseBody();
    }

    private EntityExchangeResult<byte[]> get(String uri) {
        return this.webTestClient.get().uri(uri).exchange().expectStatus().isOk().expectBody(byte[].class).returnResult();
    }

    private WebTestClient.ResponseSpec put(String uri, byte[] content) {
        return this.webTestClient.put()
                .uri(uri)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(content)
                .exchange();
    }

    private WebTestClient.ResponseSpec put(String uri, byte[] content, String entityTag) {
        return this.webTestClient.put()
                .uri(uri)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.IF_MATCH, entityTag)
                .bodyValue(content)
                .exchange();
    }

    private Resource loadBinaryResource(URI uri) throws IOException {
        var resourceSet = new ResourceSetImpl();
        resourceSet.getURIConverter().getURIHandlers().add(0, new RestfulEMFURIHandler());
        resourceSet.getPackageRegistry().put(FlowPackage.eNS_URI, FlowPackage.eINSTANCE);
        Resource resource = new XMLResourceImpl(uri);
        resourceSet.getResources().add(resource);
        resource.load(this.getBinaryOptions());
        return resource;
    }

    private Map<String, Object> getBinaryOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);
        return options;
    }

    private EObject getNamedObject(Resource resource) {
        var iterator = resource.getAllContents();
        while (iterator.hasNext()) {
            EObject object = iterator.next();
            EStructuralFeature name = object.eClass().getEStructuralFeature("name");
            if (name != null && object.eGet(name) != null) {
                return object;
            }
        }
        throw new IllegalStateException("No named object found");
    }

    private String renameFirstElement(byte[] xmi, String name) {
        return new String(xmi, StandardCharsets.UTF_8).replaceFirst("name=\"[^\"]+\"", "name=\"" + name + "\"");
    }

    private List<String> getIds(String xmi) {
        return XMI_ID_PATTERN.matcher(xmi).results().map(result -> result.group(1)).toList();
    }

    private IEMFEditingContext getEditingContext() {
        String editingContextId = this.projectEditingContextService.getEditingContextId(FLOW_PROJECT_ID).orElseThrow();
        return this.editingContextSearchService.findById(editingContextId)
                .filter(IEMFEditingContext.class::isInstance)
                .map(IEMFEditingContext.class::cast)
                .orElseThrow();
    }
}
