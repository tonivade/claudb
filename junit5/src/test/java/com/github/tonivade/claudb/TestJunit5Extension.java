/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.IntSupplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import redis.clients.jedis.Jedis;

@ExtendWith(ClauDBExtension.class)
class TestJunit5Extension {
  
  @Test
  void testExtension(IntSupplier serverPort) {
    try (Jedis jedis = new Jedis("localhost", serverPort.getAsInt(), 1000 * 60)) {
      assertEquals("PONG", jedis.ping());
    }
  }
}
