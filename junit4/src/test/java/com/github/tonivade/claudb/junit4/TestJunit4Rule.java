/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.junit4;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class TestJunit4Rule {

  @Rule
  public ClauDBRule rule = ClauDBRule.randomPort();

  @Test
  public void testRule() {
    try (Jedis jedis = new Jedis(rule.getHost(), rule.getPort(), 1000 * 5)) {
      assertEquals("PONG", jedis.ping());
    }
  }

}
