/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.string;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.string.DecrementCommand;

@CommandUnderTest(DecrementCommand.class)
public class DecrementCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withParams("a")
    .execute()
    .assertThat(RedisToken.integer(-1));

    rule.withParams("a")
    .execute()
    .assertThat(RedisToken.integer(-2));

    rule.withParams("a")
    .execute()
    .assertThat(RedisToken.integer(-3));
  }

}
