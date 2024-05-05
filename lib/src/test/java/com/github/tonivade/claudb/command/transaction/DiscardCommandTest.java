/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.transaction;

import static com.github.tonivade.resp.protocol.RedisToken.responseOk;
import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.command.Session;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(DiscardCommand.class)
public class DiscardCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.execute()
        .assertThat(responseOk())
        .verify(Session.class).getValue("tx");
  }
}
