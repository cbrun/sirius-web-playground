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

import java.util.Objects;

import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextProcessor;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.web.application.editingcontext.EditingContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Adds the Guesstimate view to Sirius Web editing contexts.
 *
 * @author cedric
 */
@Service
public class GuesstimateViewEditingContextInitializer implements IEditingContextProcessor {

    private final View guesstimateView;

    public GuesstimateViewEditingContextInitializer(@Qualifier("guesstimateView") View guesstimateView) {
        this.guesstimateView = Objects.requireNonNull(guesstimateView);
    }

    @Override
    public void preProcess(IEditingContext editingContext) {
        if (editingContext instanceof EditingContext siriusWebEditingContext) {
            siriusWebEditingContext.getViews().add(this.guesstimateView);
        }
    }
}
