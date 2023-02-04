/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.github.tonivade.claudb.ClauDB;
import com.github.tonivade.resp.RespServer;

import redis.clients.jedis.Jedis;

@ClauDBTest
class TestJunit5Extension {

  static RespServer server = ClauDB.builder().randomPort().build();

  @Test
  void testExtension() {
    try (Jedis jedis = new Jedis("localhost", server.getPort(), 1000 * 5)) {
      assertEquals("PONG", jedis.ping());
    }
  }
}
