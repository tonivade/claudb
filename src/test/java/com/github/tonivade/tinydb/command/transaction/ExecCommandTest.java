package com.github.tonivade.tinydb.command.transaction;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;

import com.github.tonivade.resp.command.DefaultRequest;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
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

    when(rule.getSession().getValue("tx")).thenReturn(Optional.of(transaction), Optional.empty());
    when(rule.getSession().removeValue("tx")).thenReturn(Optional.of(transaction));
  }

  private TransactionState createTransaction() {
    TransactionState transaction = new TransactionState();
    transaction.enqueue(new DefaultRequest(null, null, safeString("ping"), emptyList()));
    transaction.enqueue(new DefaultRequest(null, null, safeString("ping"), emptyList()));
    transaction.enqueue(new DefaultRequest(null, null, safeString("ping"), emptyList()));
    return transaction;
  }

  private static class MockCommand implements RespCommand {
    @Override
    public RedisToken execute(Request request) {
      return RedisToken.string("");
    }
  }
}
