/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.hash;

import static com.github.tonivade.claudb.DatabaseValueMatchers.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(HashDeleteCommand.class)
public class HashDeleteCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withData("key", hash(entry("a", "1")))
    .withParams("key", "a", "b", "c")
    .execute()
    .assertThat(RedisToken.integer(true));
  }

  @Test
  public void testExecuteNoKeys()  {
    rule.withData("key", hash(entry("d", "1")))
    .withParams("key", "a", "b", "c")
    .execute()
    .assertThat(RedisToken.integer(false));
  }

}
