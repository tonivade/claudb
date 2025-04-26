/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.string;

import org.junit.Rule;
import org.junit.Test;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.resp.protocol.RedisToken;

@CommandUnderTest(DecrementByCommand.class)
public class DecrementByCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withParams("a", "10")
    .execute()
    .assertThat(RedisToken.integer(-10));

    rule.withParams("a", "10")
    .execute()
    .assertThat(RedisToken.integer(-20));

    rule.withParams("a", "5")
    .execute()
    .assertThat(RedisToken.integer(-25));
  }

}
