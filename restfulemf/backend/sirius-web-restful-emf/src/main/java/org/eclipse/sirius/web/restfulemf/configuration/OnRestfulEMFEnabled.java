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

import java.util.Arrays;

import org.eclipse.sirius.web.application.configurationproperties.SiriusWebProperties;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Determines whether RESTful EMF is enabled.
 *
 * @since 2026.7.3
 */
public class OnRestfulEMFEnabled extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        var message = ConditionMessage.forCondition(this.getClass().getSimpleName()).notAvailable(RestfulEMFProperties.FEATURE);
        ConditionOutcome outcome = ConditionOutcome.noMatch(message);

        var enabled = context.getEnvironment().getProperty("sirius.web.enabled", "");
        var enabledFeatures = Arrays.stream(enabled.split(",")).map(String::trim).toList();
        if (enabledFeatures.contains(SiriusWebProperties.EVERYTHING) || enabledFeatures.contains(RestfulEMFProperties.FEATURE)) {
            outcome = ConditionOutcome.match(ConditionMessage.forCondition(this.getClass().getSimpleName()).available(RestfulEMFProperties.FEATURE));
        }

        var disabled = context.getEnvironment().getProperty("sirius.web.disabled", "");
        var disabledFeatures = Arrays.stream(disabled.split(",")).map(String::trim).toList();
        if (disabledFeatures.contains(RestfulEMFProperties.FEATURE)) {
            outcome = ConditionOutcome.noMatch(ConditionMessage.forCondition(this.getClass().getSimpleName()).notAvailable(RestfulEMFProperties.FEATURE));
        }
        return outcome;
    }
}
