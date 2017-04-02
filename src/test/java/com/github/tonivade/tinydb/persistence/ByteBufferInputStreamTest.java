/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.persistence;

import static com.github.tonivade.tinydb.persistence.HexUtil.toByteArray;
import static com.github.tonivade.tinydb.persistence.HexUtil.toHexString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

public class ByteBufferInputStreamTest {

    @Test
    public void testStream() throws IOException {
        ByteBufferInputStream in =  new ByteBufferInputStream(toByteArray("09486F6C61206D756E646F21"));

        assertThat(in.read(), is(9));

        byte[] array = new byte[in.available()];
        int readed = in.read(array);

        assertThat(readed, is(array.length));
        assertThat(toHexString(array), is("486F6C61206D756E646F21"));

        in.close();
    }

}
