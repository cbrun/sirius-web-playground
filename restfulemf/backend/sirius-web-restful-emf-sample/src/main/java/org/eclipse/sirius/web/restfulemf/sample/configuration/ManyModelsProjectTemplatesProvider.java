/*******************************************************************************
 * Copyright (c) 2023, 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.sirius.web.restfulemf.sample.configuration;

import java.util.List;

import org.eclipse.sirius.web.application.project.services.api.IProjectTemplateProvider;
import org.eclipse.sirius.web.application.project.services.api.ProjectTemplate;
import org.springframework.stereotype.Service;

/**
 * Provides the playground project templates.
 *
 * @author cbrun
 */
@Service
public class ManyModelsProjectTemplatesProvider implements IProjectTemplateProvider {

    public static final String MANY_MODELS_TEMPLATE_ID = "manymodels-template";

    public static final String ONE_MILLION_TEMPLATE_ID = "onemillion-template";

    @Override
    public List<ProjectTemplate> getProjectTemplates() {
        return List.of(
                new ProjectTemplate(MANY_MODELS_TEMPLATE_ID, "Many Models", "/project-templates/Models-Template.png", List.of()),
                new ProjectTemplate(ONE_MILLION_TEMPLATE_ID, "1M-Modeling", "/project-templates/1MModeling-Template.png", List.of()));
    }
}
