/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.redis;

import static java.util.stream.Collectors.toList;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SafeString implements Comparable<SafeString> {

    public static final SafeString EMPTY_STRING = new SafeString(new byte[] {});

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private ByteBuffer buffer;

    public SafeString(byte[] bytes) {
        Objects.nonNull(bytes);
        this.buffer = ByteBuffer.wrap(bytes);
    }

    public SafeString(ByteBuffer buffer) {
        Objects.nonNull(buffer);
        this.buffer = buffer;
    }

    public byte[] getBytes() {
        ByteBuffer copy = buffer.duplicate();
        byte[] bytes = new byte[copy.remaining()];
        copy.get(bytes);
        return bytes;
    }

    public ByteBuffer getBuffer() {
        return buffer.duplicate();
    }

    public int length() {
        return buffer.remaining();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + buffer.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SafeString other = (SafeString) obj;
        if (!buffer.equals(other.buffer)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(SafeString o) {
        // FIXME:
        return toString().compareTo(o.toString());
    }

    @Override
    public String toString() {
        return DEFAULT_CHARSET.decode(buffer.duplicate()).toString();
    }

    public static SafeString safeString(String str) {
        Objects.nonNull(str);
        return new SafeString(DEFAULT_CHARSET.encode(str));
    }

    public static List<SafeString> safeAsList(String ... strs) {
        Objects.nonNull(strs);
        return Stream.of(strs).map((item) -> safeString(item)).collect(toList());
    }

    public static SafeString append(SafeString stringA, SafeString stringB) {
        Objects.nonNull(stringA);
        Objects.nonNull(stringB);
        ByteBuffer byteBuffer = ByteBuffer.allocate(stringA.length() + stringB.length());
        byteBuffer.put(stringA.getBytes());
        byteBuffer.put(stringB.getBytes());
        byteBuffer.rewind();
        return new SafeString(byteBuffer);
    }

}
