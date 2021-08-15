package com.github.tonivade.claudb.command.string;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.claudb.data.DatabaseValue;

@CommandUnderTest(MultiSetIfNotExistsCommand.class)
public class MultiSetIfNotExistsCommandTest {
  
  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecuteNoneExists() {
    rule.withParams("a", "1", "b", "2", "c", "3")
        .execute()
        .assertValue("a", equalTo(string("1")))
        .assertValue("b", equalTo(string("2")))
        .assertValue("c", equalTo(string("3")))
        .assertThat(RedisToken.integer(1));
  }

  @Test
  public void testExecuteOneExists() {
    rule.withData(safeKey("a"), string("asdf"))
        .withParams("a", "1", "b", "2", "c", "3")
        .execute()
        .assertValue("a", equalTo(string("asdf")))
        .assertValue("b", is(DatabaseValue.NULL))
        .assertValue("c", is(DatabaseValue.NULL))
        .assertThat(RedisToken.integer(0));
  }
}
