/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.string;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(SetIfNotExistsCommand.class)
public class SetIfNotExistsCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);
  
  @Test
  public void testExecuteNotExists() {
    rule.withParams("key", "value")
        .execute()
        .assertThat(integer(true));
  }
  
  @Test
  public void testExecuteExists() {
    rule.withData(safeKey("key"), string("value1"))
        .withParams("key", "value2")
        .execute()
        .assertThat(integer(false));
  }
}
