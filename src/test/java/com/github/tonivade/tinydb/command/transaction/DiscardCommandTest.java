package com.github.tonivade.tinydb.command.transaction;

import static com.github.tonivade.resp.protocol.RedisToken.responseOk;
import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.command.Session;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(DiscardCommand.class)
public class DiscardCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.execute()
        .assertThat(responseOk())
        .verify(Session.class).getValue("tx");
  }
}
