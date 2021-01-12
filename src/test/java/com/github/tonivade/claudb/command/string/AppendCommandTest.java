/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.string;

import static com.github.tonivade.claudb.data.DatabaseValue.string;
import org.junit.Rule;
import org.junit.Test;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.resp.protocol.RedisToken;

@CommandUnderTest(AppendCommand.class)
public class AppendCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withData("test", string("Hola"))
    .withParams("test", " mundo")
    .execute()
    .assertThat(RedisToken.integer(10));
  }

  @Test
  public void testExecuteNoExists() {
    rule.withParams("test", " mundo")
    .execute()
    .assertThat(RedisToken.integer(6));
  }
}
