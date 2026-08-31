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
package org.eclipse.sirius.web.restfulemf.application.api;

import java.io.OutputStream;

/**
 * Writes a resource representation without materializing it in memory.
 *
 * @since 2026.7.3
 */
@FunctionalInterface
public interface IResourceWriter {

    void write(OutputStream outputStream);
}
