/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.hash;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.claudb.DatabaseValueMatchers.entry;
import static com.github.tonivade.claudb.command.InAnyOrderRedisArrayMatcher.containsInAnyOrder;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(HashValuesCommand.class)
public class HashValuesCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withData("test",
                  hash(entry("key1", "value1"),
                       entry("key2", "value2"),
                       entry("key3", "value3")))
    .withParams("test")
    .execute()
    .assertThat(containsInAnyOrder(string("value1"), string("value2"), string("value3")));
  }

  @Test
  public void testExecuteNotExists() {
    rule.withParams("test")
    .execute()
    .assertThat(array());
  }

}
