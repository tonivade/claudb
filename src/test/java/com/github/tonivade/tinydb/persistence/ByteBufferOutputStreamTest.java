/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.persistence;

import static com.github.tonivade.tinydb.persistence.HexUtil.toByteArray;
import static com.github.tonivade.tinydb.persistence.HexUtil.toHexString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.github.tonivade.tinydb.persistence.ByteBufferOutputStream;

public class ByteBufferOutputStreamTest {

    @Test
    public void testStream() throws Exception {
        ByteBufferOutputStream out =  new ByteBufferOutputStream(10);

        out.write(9);
        out.write(toByteArray("486F6C61206D756E646F21"));

        assertThat(toHexString(out.toByteArray()), is("09486F6C61206D756E646F21"));

        out.close();
    }

}
