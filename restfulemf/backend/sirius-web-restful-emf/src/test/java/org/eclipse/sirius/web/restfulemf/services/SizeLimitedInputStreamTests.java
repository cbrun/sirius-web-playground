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
package org.eclipse.sirius.web.restfulemf.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.junit.jupiter.api.Test;

/**
 * Verifies byte limits across all stream access paths.
 *
 * @author cbrun
 */
public class SizeLimitedInputStreamTests {

    @Test
    public void givenExactLimitWhenReadingThenEndOfStreamIsAllowed() throws IOException {
        try (var input = new SizeLimitedInputStream(new ByteArrayInputStream(new byte[] {1, 2}), 2)) {
            assertThat(input.read()).isEqualTo(1);
            assertThat(input.read(new byte[2], 0, 2)).isEqualTo(1);
            assertThat(input.read()).isEqualTo(-1);
            assertThat(input.read(new byte[1], 0, 1)).isEqualTo(-1);
        }
    }

    @Test
    public void givenMarkedStreamWhenResetThenReReadingDoesNotDoubleCount() throws IOException {
        try (var input = new SizeLimitedInputStream(new ByteArrayInputStream(new byte[] {1, 2, 3}), 3)) {
            assertThat(input.read()).isEqualTo(1);
            input.mark(3);
            assertThat(input.skip(2)).isEqualTo(2);
            input.reset();
            assertThat(input.readAllBytes()).containsExactly((byte) 2, (byte) 3);
        }
    }

    @Test
    public void givenOversizedStreamWhenSkippingOrReadingThenTheLimitCannotBeBypassed() throws IOException {
        try (var input = new SizeLimitedInputStream(new ByteArrayInputStream(new byte[] {1, 2}), 1)) {
            assertThatThrownBy(() -> input.skip(2)).isInstanceOfSatisfying(RestfulEMFException.class,
                    exception -> assertThat(exception.getError()).isEqualTo(RestfulEMFError.PAYLOAD_TOO_LARGE));
        }
        try (var input = new SizeLimitedInputStream(new ByteArrayInputStream(new byte[] {1, 2}), 1)) {
            assertThatThrownBy(input::readAllBytes).isInstanceOf(RestfulEMFException.class);
        }
        assertThatThrownBy(() -> new SizeLimitedInputStream(InputStream.nullInputStream(), 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
