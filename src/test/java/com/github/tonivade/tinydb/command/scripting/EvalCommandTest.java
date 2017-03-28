package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.integer;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(EvalCommand.class)
public class EvalCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);
  
  @Test
  public void testExecute() throws Exception {
    rule.withParams("return 1", "0")
        .execute()
        .verify().add(integer(1));
  }
}
