/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.key;

import static com.github.tonivade.claudb.command.InAnyOrderRedisArrayMatcher.containsInAnyOrder;
import static com.github.tonivade.claudb.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(KeysCommand.class)
public class KeysCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withData("abc", string("1"))
    .withData("acd", string("2"))
    .withData("c", string("3"))
    .withParams("*")
    .execute()
    .assertThat(containsInAnyOrder(RedisToken.string("abc"),
                             RedisToken.string("acd"),
                             RedisToken.string("c")));
  }

  @Test
  public void testExecuteExclamation() {
    rule.withData("abc", string("1"))
    .withData("acd", string("2"))
    .withData("c", string("3"))
    .withParams("a??")
    .execute()
    .assertThat(containsInAnyOrder(RedisToken.string("abc"),
                             RedisToken.string("acd")));
  }

  @Test
  public void testExecuteAsterisk() {
    rule.withData("abc", string("1"))
    .withData("acd", string("2"))
    .withData("c", string("3"))
    .withParams("a*")
    .execute()
    .assertThat(containsInAnyOrder(RedisToken.string("abc"),
                             RedisToken.string("acd")));
  }

}
