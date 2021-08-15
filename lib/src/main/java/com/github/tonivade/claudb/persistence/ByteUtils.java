/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.persistence;

public class ByteUtils {

  public static byte[] toByteArray(long value) {
    byte[] b = new byte[Long.BYTES];
    for (int i = 0; i < b.length; ++i) {
      b[i] = (byte) (value >> (Long.BYTES - i - 1 << 3));
    }
    return b;
  }

  public static byte[] toByteArray(int value) {
    byte[] b = new byte[Integer.BYTES];
    for (int i = 0; i < b.length; ++i) {
      b[i] = (byte) (value >> (Integer.BYTES - i - 1 << 3));
    }
    return b;
  }

  public static long byteArrayToLong(byte[] array) {
    return ((long)array[7] & 0xFF) |
        ((long)(array[6] & 0xFF)) << 8 |
        ((long)(array[5] & 0xFF)) << 16 |
        ((long)(array[4] & 0xFF)) << 24 |
        ((long)(array[3] & 0xFF)) << 32 |
        ((long)(array[2] & 0xFF)) << 40 |
        ((long)(array[1] & 0xFF)) << 48 |
        ((long)(array[0] & 0xFF)) << 56;
  }

  public static int byteArrayToInt(byte[] array) {
    return array[3] & 0xFF |
        (array[2] & 0xFF) << 8 |
        (array[1] & 0xFF) << 16 |
        (array[0] & 0xFF) << 24;
  }
}
