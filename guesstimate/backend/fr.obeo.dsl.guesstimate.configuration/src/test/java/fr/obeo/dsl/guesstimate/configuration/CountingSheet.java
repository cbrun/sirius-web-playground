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

import fr.obeo.dsl.guesstimate.impl.SheetImpl;

/**
 * A sheet exposing how many times its derived samples have been recomputed.
 *
 * @author cedric
 */
public class CountingSheet extends SheetImpl {

    private int resamplingCount;

    @Override
    public void resample() {
        this.resamplingCount++;
    }

    public int getResamplingCount() {
        return this.resamplingCount;
    }
}
