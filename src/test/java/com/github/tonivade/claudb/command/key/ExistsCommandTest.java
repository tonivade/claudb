/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.key;

import static com.github.tonivade.claudb.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(ExistsCommand.class)
public class ExistsCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withData("test", string("value"))
    .withParams("test")
    .execute()
    .assertThat(RedisToken.integer(true));
  }

  @Test
  public void testExecuteNotExists() {
    rule.withParams("test")
    .execute()
    .assertThat(RedisToken.integer(false));
  }

}
