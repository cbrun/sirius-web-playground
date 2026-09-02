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

import java.util.List;

import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.web.application.document.dto.Stereotype;
import org.eclipse.sirius.web.application.document.services.api.IStereotypeProvider;
import org.springframework.stereotype.Service;

/**
 * Contributes the Guesstimate document type.
 *
 * @author cedric
 */
@Service
public class GuesstimateStereotypeProvider implements IStereotypeProvider {

    public static final String GUESSTIMATE_STEREOTYPE_ID = "guesstimate";

    @Override
    public List<Stereotype> getStereotypes(IEditingContext editingContext) {
        if (editingContext instanceof IEMFEditingContext) {
            return List.of(new Stereotype(GUESSTIMATE_STEREOTYPE_ID, "Guesstimate"));
        }
        return List.of();
    }
}
