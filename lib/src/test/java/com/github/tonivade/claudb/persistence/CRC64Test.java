/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.persistence;

import static com.github.tonivade.claudb.persistence.ByteUtils.toByteArray;
import static com.github.tonivade.resp.protocol.SafeString.fromHexString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.github.tonivade.resp.protocol.SafeString;

public class CRC64Test {

  @Test
  public void testOne() throws UnsupportedEncodingException {
    CRC64 crc = new CRC64();
    byte[] bytes = "123456789".getBytes("UTF-8");
    for (byte b : bytes) {
      crc.update(b);
    }

    assertThat(new SafeString(toByteArray(crc.getValue())).toHexString(), is("995dc9bbdf1939fa"));
  }

  @Test
  public void testString() throws UnsupportedEncodingException {
    CRC64 crc = new CRC64();
    byte[] bytes = "This is a test of the emergency broadcast system.".getBytes("UTF-8");
    crc.update(bytes, 0, bytes.length);

    assertThat(new SafeString(toByteArray(crc.getValue())).toHexString(), is("27db187fc15bbc72"));
  }

  @Test
  public void testTest() {
    CRC64 crc = new CRC64();
    byte[] bytes = fromHexString("524544495330303033fe00ff").getBytes();
    crc.update(bytes, 0, bytes.length);

    assertThat(new SafeString(toByteArray(crc.getValue())).toHexString(), is("77de0394ac9d23ea"));
  }

}
