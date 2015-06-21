/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.redis;

import static java.util.stream.Collectors.toList;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SafeString {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private byte[] bytes;

    public SafeString(byte[] bytes) {
        Objects.nonNull(bytes);
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int length() {
        return bytes.length;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(bytes);
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
        if (!Arrays.equals(bytes, other.bytes)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new String(bytes, DEFAULT_CHARSET);
    }

    public static SafeString fromString(String str) {
        Objects.nonNull(str);
        return new SafeString(str.getBytes(DEFAULT_CHARSET));
    }

    public static List<SafeString> asList(String ... strs) {
        Objects.nonNull(strs);
        return Stream.of(strs).map((item) -> fromString(item)).collect(toList());
    }

}
