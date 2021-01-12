/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.list;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.claudb.DatabaseValueMatchers.isList;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(LeftPushCommand.class)
public class LeftPushCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withParams("key", "a", "b", "c")
        .execute()
        .assertValue("key", isList("c", "b", "a"))
        .assertThat(integer(3));

    rule.withParams("key", "d")
        .execute()
        .assertValue("key", isList("d", "c", "b", "a"))
        .assertThat(integer(4));
  }

}
