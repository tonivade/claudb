/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.string;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(MultiGetCommand.class)
public class MultiGetCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withData("a", string("1"))
    .withData("c", string("2"))
    .withParams("a", "b", "c")
    .execute()
    .then(array(RedisToken.string("1"), 
                RedisToken.string(SafeString.EMPTY_STRING),
                RedisToken.string("2")));
  }

}
