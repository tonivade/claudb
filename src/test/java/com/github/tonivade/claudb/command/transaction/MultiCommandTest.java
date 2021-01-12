/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.transaction;

import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.TransactionState;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.resp.protocol.RedisToken;

@CommandUnderTest(MultiCommand.class)
public class MultiCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void executeWithoutActiveTransaction()  {
    rule.execute()
    .assertThat(RedisToken.status("OK"));
  }

  @Test
  public void executeWithActiveTransaction()  {
    when(rule.getSession().getValue("tx")).thenReturn(Option.some(new TransactionState()));

    rule.execute()
    .assertThat(RedisToken.error("ERR MULTI calls can not be nested"));
  }
}
