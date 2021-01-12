/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.list;

import static com.github.tonivade.claudb.DatabaseValueMatchers.isList;
import static com.github.tonivade.claudb.DatabaseValueMatchers.list;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(ListSetCommand.class)
public class ListSetCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withData("key", list("a", "b", "c"))
        .withParams("key", "0", "A")
        .execute()
        .assertValue("key", isList("A", "b", "c"))
        .assertThat(RedisToken.status("OK"));

    rule.withData("key", list("a", "b", "c"))
        .withParams("key", "-1", "C")
        .execute()
        .assertValue("key", isList("a", "b", "C"))
        .assertThat(RedisToken.status("OK"));

    rule.withData("key", list("a", "b", "c"))
        .withParams("key", "z", "C")
        .execute()
        .assertValue("key", isList("a", "b", "c"))
        .assertThat(RedisToken.error("ERR value is not an integer or out of range"));

    rule.withData("key", list("a", "b", "c"))
        .withParams("key", "99", "C")
        .execute()
        .assertValue("key", isList("a", "b", "c"))
        .assertThat(RedisToken.error("ERR index out of range"));
  }

}
