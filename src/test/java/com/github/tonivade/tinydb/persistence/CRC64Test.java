/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.persistence;

import static com.github.tonivade.tinydb.persistence.ByteUtils.toByteArray;
import static com.github.tonivade.tinydb.persistence.HexUtil.toHexString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CRC64Test {

    @Test
    public void testOne() throws Exception {
        CRC64 crc = new CRC64();
        byte[] bytes = "123456789".getBytes("UTF-8");
        for (byte b : bytes) {
            crc.update(b);
        }

        assertThat(toHexString(toByteArray(crc.getValue())), is("995DC9BBDF1939FA"));
    }

    @Test
    public void testString() throws Exception {
        CRC64 crc = new CRC64();
        byte[] bytes = "This is a test of the emergency broadcast system.".getBytes("UTF-8");
        crc.update(bytes, 0, bytes.length);

        assertThat(toHexString(toByteArray(crc.getValue())), is("27DB187FC15BBC72"));
    }


    @Test
    public void testTest() throws Exception {
        CRC64 crc = new CRC64();
        byte[] bytes = HexUtil.toByteArray("524544495330303033FE00FF");
        crc.update(bytes, 0, bytes.length);

        assertThat(toHexString(toByteArray(crc.getValue())), is("77DE0394AC9D23EA"));
    }

}
