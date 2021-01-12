/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.set;

import static com.github.tonivade.claudb.DatabaseValueMatchers.set;
import static com.github.tonivade.claudb.command.InAnyOrderRedisArrayMatcher.containsInAnyOrder;
import static com.github.tonivade.resp.protocol.RedisToken.string;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(SetMembersCommand.class)
public class SetMembersCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withData("key", set("a", "b", "c"))
      .withParams("key")
      .execute()
      .assertThat(containsInAnyOrder(string("a"), string("b"), string("c")));
  }
}
