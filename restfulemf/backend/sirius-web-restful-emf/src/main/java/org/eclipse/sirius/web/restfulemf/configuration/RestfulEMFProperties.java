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

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;

/**
 * Configuration of RESTful EMF transfers.
 *
 * @since 2026.7.3
 */
@ConfigurationProperties(prefix = "sirius.web.restful-emf")
public record RestfulEMFProperties(
        @DefaultValue("false") boolean requireIfMatch,
        @DefaultValue("256MB") DataSize maxRequestSize,
        @DefaultValue("256MB") DataSize maxUncompressedSize,
        @DefaultValue("2") int maxConcurrentTransfers) {

    public static final String FEATURE = "restful-emf";

    public RestfulEMFProperties {
        Objects.requireNonNull(maxRequestSize);
        Objects.requireNonNull(maxUncompressedSize);
        if (maxRequestSize.toBytes() < 1) {
            throw new IllegalArgumentException("The maximum request size must be positive");
        }
        if (maxUncompressedSize.toBytes() < 1) {
            throw new IllegalArgumentException("The maximum uncompressed size must be positive");
        }
        if (maxConcurrentTransfers < 1) {
            throw new IllegalArgumentException("The maximum number of concurrent transfers must be positive");
        }
    }
}
