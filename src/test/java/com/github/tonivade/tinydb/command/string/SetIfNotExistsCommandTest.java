package com.github.tonivade.tinydb.command.string;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

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
