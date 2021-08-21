/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class TestJunit4Rule {
  
  @Rule
  public ClauDBRule rule = ClauDBRule.randomPort();
  
  @Test
  public void testRule() {
    try (Jedis jedis = new Jedis(rule.getHost(), rule.getPort(), 1000 * 60)) {
      assertEquals("PONG", jedis.ping());
    }
  }

}
