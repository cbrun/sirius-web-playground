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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;

/**
 * Rejects an input stream after a configured number of bytes has been read.
 *
 * @since 2026.7.3
 */
public class SizeLimitedInputStream extends FilterInputStream {

    private final long maximumSize;

    private long count;

    private long markedCount;

    public SizeLimitedInputStream(InputStream inputStream, long maximumSize) {
        super(Objects.requireNonNull(inputStream));
        if (maximumSize < 1) {
            throw new IllegalArgumentException("The maximum input size must be positive");
        }
        this.maximumSize = maximumSize;
    }

    @Override
    public int read() throws IOException {
        int value = super.read();
        if (value != -1) {
            this.increment(1);
        }
        return value;
    }

    @Override
    public int read(byte[] bytes, int offset, int length) throws IOException {
        int readCount = super.read(bytes, offset, length);
        if (readCount > 0) {
            this.increment(readCount);
        }
        return readCount;
    }

    @Override
    public long skip(long countToSkip) throws IOException {
        long skippedCount = super.skip(countToSkip);
        this.increment(skippedCount);
        return skippedCount;
    }

    @Override
    public synchronized void mark(int readLimit) {
        super.mark(readLimit);
        this.markedCount = this.count;
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
        this.count = this.markedCount;
    }

    private void increment(long increment) {
        this.count += increment;
        if (this.count > this.maximumSize) {
            throw new RestfulEMFException(RestfulEMFError.PAYLOAD_TOO_LARGE, "The request payload is too large");
        }
    }
}
