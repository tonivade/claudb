/*
 * Copyright (c) 2016-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.bitset;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;
import com.github.tonivade.claudb.data.DatabaseValue;

@CommandUnderTest(SetBitCommand.class)
public class SetBitCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecuteOne()  {
    rule.withData("test", DatabaseValue.bitset())
    .withParams("test", "10", "1")
    .execute()
    .assertThat(integer(false));
  }

  @Test
  public void testExecuteZero()  {
    rule.withData("test", DatabaseValue.bitset(10))
    .withParams("test", "10", "0")
    .execute()
    .assertThat(integer(true));
  }

  @Test
  public void testExecuteBitFormat()  {
    rule.withData("test", DatabaseValue.bitset())
    .withParams("test", "1", "a")
    .execute()
    .assertThat(error("bit or offset is not an integer"));
  }

  @Test
  public void testExecuteOffsetFormat()  {
    rule.withData("test", DatabaseValue.bitset())
    .withParams("test", "a", "0")
    .execute()
    .assertThat(error("bit or offset is not an integer"));
  }
}
