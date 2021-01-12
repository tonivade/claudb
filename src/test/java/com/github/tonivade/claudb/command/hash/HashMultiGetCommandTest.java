/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.hash;

import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.claudb.DatabaseValueMatchers.entry;
import static com.github.tonivade.claudb.command.InAnyOrderRedisArrayMatcher.containsInAnyOrder;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(HashMultiGetCommand.class)
public class HashMultiGetCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withData("a", hash(entry("key1", "value1"), entry("key2", "value2")))
    .withParams("a", "key1", "key2")
    .execute()
    .assertThat(containsInAnyOrder(string("value1"), string("value2")));
  }

}
