/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.persistence;

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

}
