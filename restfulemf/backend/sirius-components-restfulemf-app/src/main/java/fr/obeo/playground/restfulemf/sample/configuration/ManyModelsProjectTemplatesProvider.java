/*******************************************************************************
 * Copyright (c) 2023 Obeo.
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
package fr.obeo.playground.restfulemf.sample.configuration;

import java.util.List;

import org.eclipse.sirius.web.services.api.projects.IProjectTemplateProvider;
import org.eclipse.sirius.web.services.api.projects.ProjectTemplate;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class ManyModelsProjectTemplatesProvider implements IProjectTemplateProvider {

	public static final String MANYMODELS_TEMPLATE_ID = "manymodels-template";

	@Override
	public List<ProjectTemplate> getProjectTemplates() {
		return List.of(ProjectTemplate
				.newProjectTemplate(MANYMODELS_TEMPLATE_ID)
				.label("Many Models")
                .imageURL("/images/Models-Template.png")
                .natures(List.of())
				.build());
	}

}
