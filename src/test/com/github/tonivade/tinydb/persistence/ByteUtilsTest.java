/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.github.tonivade.tinydb.persistence.ByteUtils;

public class ByteUtilsTest {

    @Test
    public void testInt() throws Exception {
        byte[] array = ByteUtils.toByteArray(1234567890);

        System.out.println(HexUtil.toHexString(array));

        int i = ByteUtils.byteArrayToInt(array);

        assertThat(i, is(1234567890));
    }

    @Test
    public void testLong() throws Exception {
        byte[] array = ByteUtils.toByteArray(1234567890987654321L);

        System.out.println(HexUtil.toHexString(array));

        long l = ByteUtils.byteArrayToLong(array);

        assertThat(l, is(1234567890987654321L));
    }

}
