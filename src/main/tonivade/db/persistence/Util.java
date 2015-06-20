/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.persistence;

public class Util {

    public static byte[] toByteArray(long value) {
        byte[] b = new byte[Long.BYTES];
        for (int i = 0; i < Long.BYTES; ++i) {
          b[i] = (byte) (value >> (Long.BYTES - i - 1 << 3));
        }
        return b;
    }

    public static byte[] toByteArray(int value) {
        byte[] b = new byte[Integer.BYTES];
        for (int i = 0; i < Integer.BYTES; ++i) {
          b[i] = (byte) (value >> (Integer.BYTES - i - 1 << 3));
        }
        return b;
    }

}
