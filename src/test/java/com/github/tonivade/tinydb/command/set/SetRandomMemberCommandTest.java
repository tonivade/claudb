/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.set;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.set;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.data.DatabaseValue;

@CommandUnderTest(SetRandomMemberCommand.class)
public class SetRandomMemberCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withData("key", set("a", "b", "c"))
    .withParams("key")
    .execute()
    .assertThat(notNullValue());

    DatabaseValue value = rule.getDatabase().get(safeKey("key"));
    assertThat(value.<Set<String>>getValue().size(), is(3));
  }

  @Test
  public void testExecuteNotExists()  {
    rule.withParams("key")
    .execute()
    .assertThat(RedisToken.string(SafeString.EMPTY_STRING));
  }

}
