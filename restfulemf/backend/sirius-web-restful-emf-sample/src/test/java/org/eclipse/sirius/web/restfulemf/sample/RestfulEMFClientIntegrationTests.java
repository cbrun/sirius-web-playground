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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.web.application.project.dto.CreateProjectInput;
import org.eclipse.sirius.web.restfulemf.client.RestfulEMFClient;
import org.eclipse.sirius.web.restfulemf.sample.configuration.ManyModelsProjectTemplatesProvider;
import org.eclipse.sirius.web.tests.data.GivenSiriusWebServer;
import org.eclipse.sirius.web.tests.graphql.CreateProjectExecutor;
import org.eclipse.sirius.web.tests.graphql.CreateProjectMutationRunner;
import org.eclipse.sirius.web.tests.services.api.IGivenInitialServerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import fr.obeo.dsl.designer.sample.flow.FlowPackage;

/**
 * Exercises the standalone client through the actual Sirius Web REST endpoints.
 *
 * @author cbrun
 */
@GivenSiriusWebServer
@Import({ CreateProjectExecutor.class, CreateProjectMutationRunner.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestfulEMFClientIntegrationTests extends AbstractIntegrationTests {

    private static final String SERVER_URI = "http://localhost:";

    private static final String NAME_FEATURE = "name";

    private static final String FLOW_PROJECT_ID = "d419bbee-9cba-4b85-972c-660d875ad705";

    @LocalServerPort
    private int port;

    @Autowired
    private IGivenInitialServerState givenInitialServerState;

    @Autowired
    private CreateProjectExecutor createProjectExecutor;

    @BeforeEach
    public void beforeEach() {
        this.givenInitialServerState.initialize();
    }

    @Test
    @DisplayName("Given a Flow project URL, when loaded and saved with the client, then identifiers survive and stale saves fail")
    public void givenFlowProjectURLWhenLoadedAndSavedThenIdentifiersSurviveAndStaleSavesFail() throws IOException {
        URI uri = URI.createURI(SERVER_URI + this.port + "/projects/" + FLOW_PROJECT_ID + "/edit?selection=ignored#ignored");
        var configuredSet = new ResourceSetImpl();
        configuredSet.getPackageRegistry().put(FlowPackage.eNS_URI, FlowPackage.eINSTANCE);
        var client = new RestfulEMFClient();
        var resourceSet = client.loadProject(uri, configuredSet);
        var staleSet = new RestfulEMFClient().loadProject(uri);

        assertThat(resourceSet).isSameAs(configuredSet);
        assertThat(resourceSet.getPackageRegistry().getEPackage(FlowPackage.eNS_URI)).isSameAs(FlowPackage.eINSTANCE);
        assertThat(resourceSet.getResources()).hasSize(1);
        Resource resource = resourceSet.getResources().getFirst();
        EObject object = this.namedObject(resource);
        URI objectURI = EcoreUtil.getURI(object);
        assertThat(objectURI.scheme()).isEqualTo("http");
        object.eSet(object.eClass().getEStructuralFeature(NAME_FEATURE), "SavedByStandaloneClient");
        resource.save(Map.of());

        var reloadedSet = new RestfulEMFClient().loadProject(URI.createURI(SERVER_URI + this.port
                + "/api/rest/projects/" + FLOW_PROJECT_ID + "/documents/bin/Flow"));
        EObject reloaded = reloadedSet.getEObject(objectURI, false);
        assertThat(reloaded).isNotNull();
        assertThat(reloaded.eGet(reloaded.eClass().getEStructuralFeature(NAME_FEATURE))).isEqualTo("SavedByStandaloneClient");
        assertThat(EcoreUtil.getURI(reloaded)).isEqualTo(objectURI);
        assertThatThrownBy(() -> staleSet.getResources().getFirst().save(Map.of()))
                .isInstanceOf(IOException.class).hasMessageContaining("412");
    }

    @Test
    @Transactional
    @DisplayName("Given a Many Models project, when loaded from a workbench sub-URL, then every semantic document is loaded")
    public void givenManyModelsProjectWhenLoadedFromWorkbenchSubURLThenEveryDocumentIsLoaded(CapturedOutput capturedOutput) throws IOException {
        var input = new CreateProjectInput(UUID.randomUUID(), "Client integration", ManyModelsProjectTemplatesProvider.MANY_MODELS_TEMPLATE_ID, List.of());
        var projectId = this.createProjectExecutor.execute(input, capturedOutput).isSuccess().getProjectId();
        var webClient = WebTestClient.bindToServer().baseUrl(SERVER_URI + this.port).build();
        List<Map<String, Object>> documents = webClient.get().uri("/api/rest/projects/{projectId}/documents", projectId)
                .exchange().expectStatus().isOk()
                .expectBody(new org.springframework.core.ParameterizedTypeReference<List<Map<String, Object>>>() { }).returnResult().getResponseBody();

        var resourceSet = new RestfulEMFClient().loadProject(URI.createURI(SERVER_URI + this.port + "/projects/" + projectId + "/edit"));

        assertThat(documents).hasSize(5);
        assertThat(resourceSet.getResources()).hasSize(5).allSatisfy(resource -> {
            assertThat(resource.isLoaded()).isTrue();
            assertThat(resource.getContents()).isNotEmpty();
            assertThat(resource.getURI().scheme()).isEqualTo("http");
        });
        assertThat(resourceSet.getResources().stream().map(resource -> URI.decode(resource.getURI().lastSegment())).toList())
                .containsExactlyElementsOf(documents.stream().map(document -> document.get("path").toString()).sorted().toList());
    }

    private EObject namedObject(Resource resource) {
        var contents = resource.getAllContents();
        while (contents.hasNext()) {
            EObject object = contents.next();
            var name = object.eClass().getEStructuralFeature(NAME_FEATURE);
            if (name != null && object.eGet(name) != null) {
                return object;
            }
        }
        throw new AssertionError("The Flow model must contain a named object");
    }
}
