/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.list;

import static com.github.tonivade.tinydb.DatabaseValueMatchers.list;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(ListIndexCommand.class)
public class ListIndexCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() throws Exception {
    rule.withData("key", list("a", "b", "c"))
    .withParams("key", "0")
    .execute()
    .then(RedisToken.string("a"));

    rule.withData("key", list("a", "b", "c"))
    .withParams("key", "-1")
    .execute()
    .then(RedisToken.string("c"));

    rule.withData("key", list("a", "b", "c"))
    .withParams("key", "-4")
    .execute()
    .then(RedisToken.string(SafeString.EMPTY_STRING));

    rule.withData("key", list("a", "b", "c"))
    .withParams("key", "4")
    .execute()
    .then(RedisToken.string(SafeString.EMPTY_STRING));

    rule.withData("key", list("a", "b", "c"))
    .withParams("key", "a")
    .execute()
    .then(RedisToken.error("ERR value is not an integer or out of range"));
  }

}
