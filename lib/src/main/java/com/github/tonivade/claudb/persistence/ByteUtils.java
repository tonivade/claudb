/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.persistence;

import java.util.function.IntSupplier;

public class ByteUtils {

  static byte[] toByteArray(long value) {
    byte[] b = new byte[Long.BYTES];
    for (int i = 0; i < b.length; ++i) {
      b[i] = (byte) (value >> (Long.BYTES - i - 1 << 3));
    }
    return b;
  }

  static byte[] toByteArray(int value) {
    byte[] b = new byte[Integer.BYTES];
    for (int i = 0; i < b.length; ++i) {
      b[i] = (byte) (value >> (Integer.BYTES - i - 1 << 3));
    }
    return b;
  }

  static long byteArrayToLong(byte[] array) {
    return ((long)array[7] & 0xFF) |
        ((long)(array[6] & 0xFF)) << 8 |
        ((long)(array[5] & 0xFF)) << 16 |
        ((long)(array[4] & 0xFF)) << 24 |
        ((long)(array[3] & 0xFF)) << 32 |
        ((long)(array[2] & 0xFF)) << 40 |
        ((long)(array[1] & 0xFF)) << 48 |
        ((long)(array[0] & 0xFF)) << 56;
  }

  static int byteArrayToInt(byte[] array) {
    return array[3] & 0xFF |
        (array[2] & 0xFF) << 8 |
        (array[1] & 0xFF) << 16 |
        (array[0] & 0xFF) << 24;
  }

  public static byte[] lengthToByteArray(int length) {
    if (length < 0x40) {
      // 1 byte: 00XXXXXX
      return new byte[] { (byte) length };
    } else if (length < 0x4000) {
      // 2 bytes: 01XXXXXX XXXXXXXX
      int b1 = length >> 8;
      int b2 = length & 0xFF;
      return new byte[] { (byte) (0x40 | b1), (byte) b2 };
    } else {
      // 5 bytes: 10...... XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
      byte[] array = toByteArray(length);
      byte[] result = new byte[5];
      result[0] = (byte) 0x80;
      System.arraycopy(array, 0, result, 1, array.length);
      return result;
    }
  }

  public static int byteArrayToLength(IntSupplier read) {
    int length = read.getAsInt() & 0xFF;
    if (length < 0x40) {
      // 1 byte: 00XXXXXX
      return length;
    } else if (length < 0x80) {
      // 2 bytes: 01XXXXXX XXXXXXXX
      int next = read.getAsInt();
      return ((length & 0x3F) << 8) | (next & 0xFF);
    } else {
      // 5 bytes: 10...... XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
      return byteArrayToInt(new byte[] { (byte) read.getAsInt(), (byte) read.getAsInt(), (byte) read.getAsInt(), (byte) read.getAsInt() });
    }
  }
}
