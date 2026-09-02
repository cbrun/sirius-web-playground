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

import org.eclipse.sirius.components.core.api.IImagePathService;
import org.springframework.stereotype.Service;

/**
 * Provides the image paths used by Guesstimate representations.
 *
 * @author cedric
 */
@Service
public class ImagePathService implements IImagePathService {

    private static final List<String> IMAGES_PATHS = List.of("/img", "/images", "/icons");

    @Override
    public List<String> getPaths() {
        return IMAGES_PATHS;
    }

}
