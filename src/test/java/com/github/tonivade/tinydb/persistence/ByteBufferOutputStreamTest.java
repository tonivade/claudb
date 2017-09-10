/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.persistence;

import static com.github.tonivade.resp.protocol.SafeString.fromHexString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.github.tonivade.resp.protocol.SafeString;

public class ByteBufferOutputStreamTest {

  @Test
  public void testStream() throws IOException {
    ByteBufferOutputStream out =  new ByteBufferOutputStream(10);

    out.write(9);
    out.write(fromHexString("486F6C61206D756E646F21").getBytes());

    assertThat(new SafeString(out.toByteArray()).toHexString(), is("09486F6C61206D756E646F21"));

    out.close();
  }

}
