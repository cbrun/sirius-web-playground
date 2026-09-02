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
package fr.obeo.dsl.guesstimate.configuration;

import fr.obeo.dsl.guesstimate.views.GuesstimateViews;

import org.eclipse.sirius.components.view.View;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configures the Guesstimate customizations of Sirius Web.
 *
 * @author cedric
 */
@Configuration(proxyBeanMethods = false)
@Import({
    GuesstimateAutomaticModelUpdate.class,
    GuesstimateEMFConfiguration.class,
    GuesstimateJavaServiceProvider.class,
    GuesstimatePropertiesConfigurer.class,
    GuesstimateStereotypeHandler.class,
    GuesstimateStereotypeProvider.class,
    GuesstimateViewEditingContextInitializer.class,
    ImagePathService.class
})
public class GuesstimateConfiguration {

    @Bean
    public View guesstimateView() {
        return new GuesstimateViews().create();
    }
}
