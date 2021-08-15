/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.server;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(SelectCommand.class)
public class SelectCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withParams("10")
    .execute()
    .assertThat(RedisToken.status("OK"));

    assertThat(rule.getSessionState().getCurrentDB(), is(10));
  }

  @Test
  public void testExecuteWithInvalidParam() {
    rule.withParams("asdfsdf")
    .execute()
    .assertThat(RedisToken.error("ERR invalid DB index"));
  }

}
