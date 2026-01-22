/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.server;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(RoleCommand.class)
public class RoleCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void executeWithoutSlaves() {
    rule.execute()
        .assertThat(array(string("master"), integer(0), array()));
  }
}
