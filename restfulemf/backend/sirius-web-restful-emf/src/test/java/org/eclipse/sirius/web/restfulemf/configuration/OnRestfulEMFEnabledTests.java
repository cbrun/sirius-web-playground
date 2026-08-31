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
package org.eclipse.sirius.web.restfulemf.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.sirius.web.restfulemf.controllers.RestfulEMFResourceController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.mock.env.MockEnvironment;

/**
 * Tests the RESTful EMF feature activation.
 */
public class OnRestfulEMFEnabledTests {

    @Test
    public void givenTheFeatureIsDisabledThenTheAutoConfigurationDoesNotRegisterRestfulEMFBeans() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(RestfulEMFAutoConfiguration.class))
                .withPropertyValues("sirius.web.enabled=*", "sirius.web.disabled=restful-emf")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(RestfulEMFProperties.class);
                    assertThat(context).doesNotHaveBean(RestfulEMFResourceController.class);
                });
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "restful-emf |                 | true",
            "*           |                 | true",
            "*           | restful-emf     | false",
            "             |                 | false"
    })
    public void givenSiriusFeaturePropertiesThenActivationMatches(String enabled, String disabled, boolean expectedMatch) {
        var environment = new MockEnvironment()
                .withProperty("sirius.web.enabled", enabled == null ? "" : enabled)
                .withProperty("sirius.web.disabled", disabled == null ? "" : disabled);
        var context = mock(ConditionContext.class);
        when(context.getEnvironment()).thenReturn(environment);

        var outcome = new OnRestfulEMFEnabled().getMatchOutcome(context, mock(AnnotatedTypeMetadata.class));

        assertThat(outcome.isMatch()).isEqualTo(expectedMatch);
    }
}
