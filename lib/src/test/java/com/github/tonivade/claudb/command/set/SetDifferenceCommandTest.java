/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.set;

import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.claudb.DatabaseValueMatchers.set;
import static com.github.tonivade.claudb.command.InAnyOrderRedisArrayMatcher.containsInAnyOrder;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(SetDifferenceCommand.class)
public class SetDifferenceCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withData("a", set("1", "2", "3"))
    .withData("b", set("3", "4"))
    .withParams("a", "b")
    .execute()
    .assertThat(containsInAnyOrder(string("1"), string("2")));
  }

  @Test
  public void testExecuteNoExists()  {
    rule.withData("a", set("1", "2", "3"))
    .withParams("a", "b")
    .execute()
    .assertThat(containsInAnyOrder(string("1"), string("2"), string("3")));
  }

}
