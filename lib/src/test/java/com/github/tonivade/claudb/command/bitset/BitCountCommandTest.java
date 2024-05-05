/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.bitset;

import static com.github.tonivade.resp.protocol.RedisToken.integer;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.claudb.data.DatabaseValue;

@CommandUnderTest(BitCountCommand.class)
public class BitCountCommandTest {
  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withData("test", DatabaseValue.bitset(1, 5, 10, 15))
    .withParams("test")
    .execute()
    .assertThat(integer(4));
  }
}
