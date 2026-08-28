/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package fr.obeo.playground.restfulemf.sample;

import static org.assertj.core.api.Assertions.assertThat;

import fr.obeo.playground.restfulemf.sample.configuration.ManyModelsProjectTemplatesProvider;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.eclipse.sirius.components.core.api.IEditingContextSearchService;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.web.application.project.dto.CreateProjectInput;
import org.eclipse.sirius.web.application.project.services.api.IProjectTemplateProvider;
import org.eclipse.sirius.web.domain.boundedcontexts.projectsemanticdata.ProjectSemanticData;
import org.eclipse.sirius.web.domain.boundedcontexts.projectsemanticdata.services.api.IProjectSemanticDataSearchService;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests of the playground project templates.
 */
@Transactional
@GivenSiriusWebServer
@Import({ CreateProjectExecutor.class, CreateProjectMutationRunner.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectTemplatesIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    private IGivenInitialServerState givenInitialServerState;

    @Autowired
    private List<IProjectTemplateProvider> projectTemplateProviders;

    @Autowired
    private CreateProjectExecutor createProjectExecutor;

    @Autowired
    private IEditingContextSearchService editingContextSearchService;

    @Autowired
    private IProjectSemanticDataSearchService projectSemanticDataSearchService;

    @BeforeEach
    public void beforeEach() {
        this.givenInitialServerState.initialize();
    }

    @Test
    @DisplayName("Given the playground application, when templates are requested, then all starter templates and models are available")
    public void givenPlaygroundApplicationWhenTemplatesAreRequestedThenAllStarterTemplatesAndModelsAreAvailable() throws IOException {
        assertThat(this.projectTemplateProviders.stream().flatMap(provider -> provider.getProjectTemplates().stream()).map(template -> template.id()))
                .contains("blank-project", "studio-template", "blank-studio-template", "flow-template",
                        ManyModelsProjectTemplatesProvider.MANY_MODELS_TEMPLATE_ID,
                        ManyModelsProjectTemplatesProvider.ONE_MILLION_TEMPLATE_ID);

        for (int index = 1; index <= 20; index++) {
            assertThat(new ClassPathResource("1Modeling/reverse" + index + ".ecorebin").contentLength()).isPositive();
        }
    }

    @Test
    @DisplayName("Given the Many Models template, when a project is created, then all five bundled models are imported")
    public void givenManyModelsTemplateWhenProjectIsCreatedThenAllFiveBundledModelsAreImported(CapturedOutput capturedOutput) {
        var input = new CreateProjectInput(UUID.randomUUID(), "Many Models", ManyModelsProjectTemplatesProvider.MANY_MODELS_TEMPLATE_ID, List.of());
        var projectId = this.createProjectExecutor.execute(input, capturedOutput).isSuccess().getProjectId();

        var resourceNames = this.projectSemanticDataSearchService.findByProjectId(AggregateReference.to(projectId))
                .map(ProjectSemanticData::getSemanticData)
                .map(AggregateReference::getId)
                .map(UUID::toString)
                .flatMap(this.editingContextSearchService::findById)
                .filter(IEMFEditingContext.class::isInstance)
                .map(IEMFEditingContext.class::cast)
                .stream()
                .flatMap(editingContext -> editingContext.getDomain().getResourceSet().getResources().stream())
                .flatMap(resource -> resource.eAdapters().stream())
                .filter(ResourceMetadataAdapter.class::isInstance)
                .map(ResourceMetadataAdapter.class::cast)
                .map(ResourceMetadataAdapter::getName)
                .toList();

        assertThat(resourceNames).containsExactlyInAnyOrder("NobelPrize.bpmn", "Big_Guy.flow", "linux-kernel.uml", "library.ecore", "reverse1.ecorebin");
    }
}
