/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.hash;

import static com.github.tonivade.claudb.DatabaseValueMatchers.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(HashExistsCommand.class)
public class HashExistsCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withData("key", hash(entry("a", "1")))
    .withParams("key", "a")
    .execute()
    .assertThat(RedisToken.integer(true));
  }

}
