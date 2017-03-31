/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.set;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.set;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(SetUnionCommand.class)
public class SetUnionCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() throws Exception {
    rule.withData("a", set("1", "2", "3"))
    .withData("b", set("3", "4"))
    .withParams("a", "b")
    .execute()
    .then(array(string("1"), string("2"), string("3"), string("4")));
  }

  @Test
  public void testExecuteNoExists() throws Exception {
    rule.withData("a", set("1", "2", "3"))
    .withParams("a", "b")
    .execute()
    .then(array(string("1"), string("2"), string("3")));
  }

}
