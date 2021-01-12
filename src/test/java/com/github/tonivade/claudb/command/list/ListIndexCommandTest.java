/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.list;

import static com.github.tonivade.claudb.DatabaseValueMatchers.list;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(ListIndexCommand.class)
public class ListIndexCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withData("key", list("a", "b", "c"))
        .withParams("key", "0")
        .execute()
        .assertThat(RedisToken.string("a"));

    rule.withData("key", list("a", "b", "c"))
        .withParams("key", "-1")
        .execute()
        .assertThat(RedisToken.string("c"));

    rule.withData("key", list("a", "b", "c"))
        .withParams("key", "-4")
        .execute()
        .assertThat(RedisToken.nullString());

    rule.withData("key", list("a", "b", "c"))
        .withParams("key", "4")
        .execute()
        .assertThat(RedisToken.nullString());

    rule.withData("key", list("a", "b", "c"))
        .withParams("key", "a")
        .execute()
        .assertThat(RedisToken.error("ERR value is not an integer or out of range"));
  }

}
