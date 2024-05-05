/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
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
import com.github.tonivade.resp.command.DefaultRequest;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import java.util.Collections;
import java.util.Optional;

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
      .thenReturn(Optional.of(transaction))
      .thenReturn(Optional.empty());
    when(rule.getSession().removeValue("tx"))
      .thenReturn(Optional.of(transaction));
  }

  private TransactionState createTransaction() {
    TransactionState transaction = new TransactionState();
    transaction.enqueue(new DefaultRequest(null, null, safeString("ping"), Collections.emptyList()));
    transaction.enqueue(new DefaultRequest(null, null, safeString("ping"), Collections.emptyList()));
    transaction.enqueue(new DefaultRequest(null, null, safeString("ping"), Collections.emptyList()));
    return transaction;
  }

  private static class MockCommand implements RespCommand {
    @Override
    public RedisToken execute(Request request) {
      return RedisToken.string("");
    }
  }
}
