package com.github.tonivade.tinydb.command.string;

import static com.github.tonivade.tinydb.DatabaseKeyMatchers.isExpired;
import static com.github.tonivade.tinydb.DatabaseKeyMatchers.isNotExpired;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.notNullValue;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.nullValue;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(SetExpiredCommand.class)
public class SetExpiredCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() throws Exception {
    rule.withParams("a", "1", "1")
    .execute()
    .assertValue("a", is(string("1")))
    .then(RedisToken.status("OK"));

    Thread.sleep(500);
    rule.assertKey("a", isNotExpired())
    .assertValue("a", notNullValue());

    Thread.sleep(500);
    rule.assertKey("a", isExpired())
    .assertValue("a", nullValue());
  }

}
