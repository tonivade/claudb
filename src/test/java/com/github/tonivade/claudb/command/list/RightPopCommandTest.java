/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.list;

import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.claudb.DatabaseValueMatchers.isList;
import static com.github.tonivade.claudb.DatabaseValueMatchers.list;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(RightPopCommand.class)
public class RightPopCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withData("key", list("a", "b", "c"))
        .withParams("key")
        .execute()
        .assertValue("key", isList("a", "b"))
        .assertThat(string("c"));

    rule.withData("key", list())
        .withParams("key")
        .execute()
        .assertThat(nullString());
  }

}
