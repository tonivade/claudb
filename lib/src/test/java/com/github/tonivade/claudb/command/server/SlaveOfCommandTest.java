/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.server;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.ClauDBRule;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

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
