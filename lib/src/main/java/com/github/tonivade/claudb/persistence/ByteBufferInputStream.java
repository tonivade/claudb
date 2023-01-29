/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.persistence;

import static com.github.tonivade.resp.util.Precondition.checkNonNull;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream {

  private final ByteBuffer buffer;

  public ByteBufferInputStream(byte[] array) {
    this(ByteBuffer.wrap(checkNonNull(array)));
  }

  public ByteBufferInputStream(ByteBuffer buffer) {
    this.buffer = checkNonNull(buffer);
  }

  @Override
  public int read() {
    if (!buffer.hasRemaining()) {
      return -1;
    }
    return buffer.get() & 0xFF;
  }

  @Override
  public int read(byte[] b, int off, int len) {
    int available = Math.min(len, buffer.remaining());
    buffer.get(b, off, available);
    return available;
  }

  @Override
  public int available() {
    return buffer.remaining();
  }
}
