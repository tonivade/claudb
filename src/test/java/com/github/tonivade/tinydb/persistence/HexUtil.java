/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.persistence;

public class HexUtil {

    private static final char[] CHARS = "0123456789ABCDEF".toCharArray();

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            sb.append(CHARS[v >>> 4]).append(CHARS[v & 0x0F]);
        }
        return sb.toString();
    }

    public static byte[] toByteArray(String string) {
        byte[] array = new byte[string.length() / 2];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) Integer.parseInt(string.substring((i * 2), (i * 2) + 2), 16);
        }
        return array;
    }

}
