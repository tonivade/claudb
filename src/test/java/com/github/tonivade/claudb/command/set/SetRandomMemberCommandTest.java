/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.set;

import static com.github.tonivade.claudb.DatabaseValueMatchers.set;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.claudb.data.DatabaseValue;

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
    assertThat(value.size(), is(3));
  }

  @Test
  public void testExecuteNotExists()  {
    rule.withParams("key")
    .execute()
    .assertThat(RedisToken.nullString());
  }

}
