/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.transaction;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.tonivade.claudb.TransactionState;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.purefun.data.ImmutableArray;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.resp.command.DefaultRequest;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;

@CommandUnderTest(ExecCommand.class)
public class ExecCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  private final RespCommand command = Mockito.spy(new MockCommand());

  @Test
  public void executeWithActiveTransaction()  {
    givenPingCommand();
    givenExistingTransaction();

    rule.execute()
    .assertThat(array(string(""), string(""), string("")));

    verify(command, times(3)).execute(any());
  }

  @Test
  public void executeWithoutActiveTransaction()  {
    rule.execute()
    .assertThat(RedisToken.error("ERR EXEC without MULTI"));
  }

  private void givenPingCommand() {
    when(rule.getServer().getCommand("ping")).thenReturn(command);
  }

  private void givenExistingTransaction() {
    TransactionState transaction = createTransaction();

    when(rule.getSession().getValue("tx"))
      .thenReturn(Option.some(transaction))
      .thenReturn(Option.none());
    when(rule.getSession().removeValue("tx"))
      .thenReturn(Option.some(transaction));
  }

  private TransactionState createTransaction() {
    TransactionState transaction = new TransactionState();
    transaction.enqueue(new DefaultRequest(null, null, safeString("ping"), ImmutableArray.empty()));
    transaction.enqueue(new DefaultRequest(null, null, safeString("ping"), ImmutableArray.empty()));
    transaction.enqueue(new DefaultRequest(null, null, safeString("ping"), ImmutableArray.empty()));
    return transaction;
  }

  private static class MockCommand implements RespCommand {
    @Override
    public RedisToken execute(Request request) {
      return RedisToken.string("");
    }
  }
}
