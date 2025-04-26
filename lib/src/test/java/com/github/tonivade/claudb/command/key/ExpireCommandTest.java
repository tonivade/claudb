/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.key;

import static com.github.tonivade.claudb.DatabaseValueMatchers.isNotExpired;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(ExpireCommand.class)
public class ExpireCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withData("test", string("value"))
    .withParams("test", "10")
    .execute()
    .assertValue("test", isNotExpired())
    .assertValue("test", is(string("value")))
    .assertThat(RedisToken.integer(true));

    rule.withParams("notExists", "10")
    .execute()
    .assertThat(RedisToken.integer(false));
  }

}
