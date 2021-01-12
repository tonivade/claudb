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

public class ByteBufferInputStreamTest {

  @Test
  public void testStream() throws IOException {
    ByteBufferInputStream in =  new ByteBufferInputStream(fromHexString("09486F6C61206D756E646F21").getBytes());

    assertThat(in.read(), is(9));

    byte[] array = new byte[in.available()];
    int readed = in.read(array);

    assertThat(readed, is(array.length));
    assertThat(new SafeString(array).toHexString(), is("486f6c61206d756e646f21"));

    in.close();
  }

}
