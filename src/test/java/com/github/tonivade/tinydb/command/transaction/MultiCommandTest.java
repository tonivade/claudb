package com.github.tonivade.tinydb.command.transaction;

import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.TransactionState;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(MultiCommand.class)
public class MultiCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void executeWithoutActiveTransaction() throws Exception {
    rule.execute()
    .then(RedisToken.status("OK"));
  }

  @Test
  public void executeWithActiveTransaction() throws Exception {
    when(rule.getSession().getValue("tx")).thenReturn(Optional.of(new TransactionState()));

    rule.execute()
    .then(RedisToken.error("ERR MULTI calls can not be nested"));
  }
}
