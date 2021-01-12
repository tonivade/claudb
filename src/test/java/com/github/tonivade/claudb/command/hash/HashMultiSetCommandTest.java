/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.hash;

import static com.github.tonivade.claudb.DatabaseValueMatchers.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.resp.protocol.RedisToken;

@CommandUnderTest(HashMultiSetCommand.class)
public class HashMultiSetCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withData("a", hash(entry("key", "value")))
    .withParams("a", "key1", "value1", "key2", "value2", "key3", "value3")
    .execute()
    .assertThat(RedisToken.status("OK"));
  }

}
