/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.persistence;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream {

    private ByteBuffer buffer;

    public ByteBufferOutputStream() {
        this(1024);
    }

    public ByteBufferOutputStream(int capacity) {
        this.buffer = ByteBuffer.allocate(capacity);
    }

    @Override
    public void write(int b) throws IOException {
        buffer.put((byte) b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        buffer.put(b, off, len);
    }

    public byte[] toByteArray() {
        byte[] array = new byte[buffer.position()];
        buffer.rewind();
        buffer.get(array);
        return array;
    }

}
