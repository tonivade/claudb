/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.persistence;

import static com.github.tonivade.resp.protocol.SafeString.fromHexString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.github.tonivade.resp.protocol.SafeString;

public class ByteBufferOutputStreamTest {

  @Test
  public void testStream() throws IOException {
    ByteBufferOutputStream out =  new ByteBufferOutputStream(10);

    out.write(9);
    out.write(fromHexString("486f6c61206d756e646f21").getBytes());

    assertThat(new SafeString(out.toByteArray()).toHexString(), is("09486f6c61206d756e646f21"));

    out.close();
  }

}
