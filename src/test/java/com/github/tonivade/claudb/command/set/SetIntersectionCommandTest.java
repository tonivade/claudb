/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.set;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.claudb.DatabaseValueMatchers.set;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(SetIntersectionCommand.class)
public class SetIntersectionCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withData("a", set("1", "2", "3"))
    .withData("b", set("3", "4"))
    .withParams("a", "b")
    .execute()
    .assertThat(array(string("3")));
  }

  @Test
  public void testExecuteNoExists()  {
    rule.withData("a", set("1", "2", "3"))
    .withParams("a", "b")
    .execute()
    .assertThat(array());
  }

}
