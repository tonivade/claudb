package com.github.tonivade.tinydb.command.transaction;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.resp.command.ICommand;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.TransactionState;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(ExecCommand.class)
public class ExecCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Captor
  private ArgumentCaptor<Collection<?>> captor;

  private final ICommand command = mock(ICommand.class);

  @Test
  public void executeWithActiveTransaction() throws Exception {
    givenPingCommand();
    givenExistingTransaction();

    rule.execute()
    .then(array(string(""), string(""), string("")));

    verify(command, times(3)).execute(any());
  }

  @Test
  public void executeWithoutActiveTransaction() throws Exception {
    rule.execute()
    .then(RedisToken.error("ERR EXEC without MULTI"));
  }

  private void givenPingCommand() {
    when(rule.getServer().getCommand("ping")).thenReturn(command);
    when(command.execute(any())).thenReturn(string(""));
  }

  private void givenExistingTransaction() {
    TransactionState transaction = createTransaction();

    when(rule.getSession().getValue("tx")).thenReturn(Optional.of(transaction), Optional.empty());
    when(rule.getSession().removeValue("tx")).thenReturn(Optional.of(transaction));
  }

  private TransactionState createTransaction() {
    TransactionState transaction = new TransactionState();
    transaction.enqueue(new Request(null, null, safeString("ping"), null));
    transaction.enqueue(new Request(null, null, safeString("ping"), null));
    transaction.enqueue(new Request(null, null, safeString("ping"), null));
    return transaction;
  }
}
