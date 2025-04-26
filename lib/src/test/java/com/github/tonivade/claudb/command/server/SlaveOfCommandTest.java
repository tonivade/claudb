/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.server;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.claudb.junit4.ClauDBRule;

@CommandUnderTest(SlaveOfCommand.class)
public class SlaveOfCommandTest {

  @Rule
  public final ClauDBRule server = ClauDBRule.randomPort();

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withParams(server.getHost(), String.valueOf(server.getPort()))
    .execute()
    .assertThat(RedisToken.status("OK"));
  }

}
