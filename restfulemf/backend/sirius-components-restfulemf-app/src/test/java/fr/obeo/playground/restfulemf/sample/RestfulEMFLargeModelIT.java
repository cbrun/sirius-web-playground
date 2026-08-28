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
package fr.obeo.playground.restfulemf.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.sirius.web.application.project.dto.CreateProjectInput;
import org.eclipse.sirius.web.tests.data.GivenSiriusWebServer;
import org.eclipse.sirius.web.tests.graphql.CreateProjectExecutor;
import org.eclipse.sirius.web.tests.graphql.CreateProjectMutationRunner;
import org.eclipse.sirius.web.tests.services.api.IGivenInitialServerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import fr.obeo.playground.restfulemf.RestfulEMFURIHandler;
import fr.obeo.playground.restfulemf.sample.configuration.ManyModelsProjectTemplatesProvider;

/**
 * Opt-in integration test for the 1M-Modeling project.
 */
@GivenSiriusWebServer
@Import({ CreateProjectExecutor.class, CreateProjectMutationRunner.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class RestfulEMFLargeModelIT extends AbstractIntegrationTests {

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
    public void givenOneMillionModelingProjectWhenABinaryResourceIsLoadedAndSavedThenTheTransferSucceeds(CapturedOutput capturedOutput) throws Exception {
        var input = new CreateProjectInput(UUID.randomUUID(), "RESTful EMF large model test",
                ManyModelsProjectTemplatesProvider.ONE_MILLION_TEMPLATE_ID, List.of());
        var projectId = this.createProjectExecutor.execute(input, capturedOutput).isSuccess().getProjectId();
        var webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port).build();
        Map<?, ?> documents = webTestClient.get()
                .uri("/api/rest/projects/{projectId}/documents", projectId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody();
        assertThat(documents).hasSize(20);

        String documentId = documents.keySet().iterator().next().toString();
        URI uri = URI.createURI("http://localhost:" + this.port + "/api/rest/projects/" + projectId + "/" + documentId + "/bin");
        var resourceSet = new ResourceSetImpl();
        resourceSet.getURIConverter().getURIHandlers().add(0, new RestfulEMFURIHandler());
        resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        Resource resource = new XMLResourceImpl(uri);
        resourceSet.getResources().add(resource);
        Map<String, Object> options = new HashMap<>();
        options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);

        resource.load(options);
        int objectCount = 0;
        var iterator = resource.getAllContents();
        while (iterator.hasNext()) {
            iterator.next();
            objectCount++;
        }
        assertThat(objectCount).isGreaterThan(10_000);
        resource.save(options);
    }
}
