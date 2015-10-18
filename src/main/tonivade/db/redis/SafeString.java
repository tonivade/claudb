/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.redis;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static tonivade.equalizer.Equalizer.equalizer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SafeString implements Comparable<SafeString> {

    public static final SafeString EMPTY_STRING = new SafeString(new byte[] {});

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final ByteBuffer buffer;

    public SafeString(byte[] bytes) {
        this.buffer = ByteBuffer.wrap(requireNonNull(bytes));
    }

    public SafeString(ByteBuffer buffer) {
        this.buffer = requireNonNull(buffer);
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
        return Objects.hash(buffer);
    }

    @Override
    public boolean equals(Object obj) {
        return equalizer(this)
                .append((one, other) -> Objects.equals(one.buffer, other.buffer))
                    .applyTo(obj);
    }

    @Override
    public int compareTo(SafeString o) {
        return compare(getBytes(), o.getBytes());
    }

    private int compare(byte[] left, byte[] right) {
        for (int i = 0, j = 0; i < left.length && j < right.length; i++, j++) {
            int a = (left[i] & 0xff);
            int b = (right[j] & 0xff);
            if (a != b) {
                return a - b;
            }
        }
        return left.length - right.length;
    }

    @Override
    public String toString() {
        return DEFAULT_CHARSET.decode(buffer.duplicate()).toString();
    }

    public static SafeString safeString(String str) {
        return new SafeString(DEFAULT_CHARSET.encode(requireNonNull(str)));
    }

    public static List<SafeString> safeAsList(String ... strs) {
        return Stream.of(requireNonNull(strs)).map((item) -> safeString(item)).collect(toList());
    }

    public static SafeString append(SafeString stringA, SafeString stringB) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(
                requireNonNull(stringA).length() + requireNonNull(stringB).length());
        byteBuffer.put(stringA.getBytes());
        byteBuffer.put(stringB.getBytes());
        byteBuffer.rewind();
        return new SafeString(byteBuffer);
    }

}
