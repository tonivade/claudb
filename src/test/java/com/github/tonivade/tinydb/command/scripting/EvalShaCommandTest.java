package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.SafeString.safeString;

import java.util.NoSuchElementException;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(EvalShaCommand.class)
public class EvalShaCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test(expected = NoSuchElementException.class)
  public void testNotExistingScript() {
    rule.withParams("notExists", "0")
        .execute();
  }

  @Test
  public void testExistingScript() {
    rule.getServerState().saveScript(safeString("test"), safeString("return nil"));

    rule.withParams("test", "0")
        .execute()
        .assertThat(nullString());
  }
}
