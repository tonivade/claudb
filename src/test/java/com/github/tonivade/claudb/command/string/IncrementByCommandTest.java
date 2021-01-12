/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.string;

import org.junit.Rule;
import org.junit.Test;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.resp.protocol.RedisToken;

@CommandUnderTest(IncrementByCommand.class)
public class IncrementByCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withParams("a", "10")
    .execute()
    .assertThat(RedisToken.integer(10));

    rule.withParams("a", "10")
    .execute()
    .assertThat(RedisToken.integer(20));

    rule.withParams("a", "5")
    .execute()
    .assertThat(RedisToken.integer(25));
  }

}
