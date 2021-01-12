/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.set;

import static com.github.tonivade.claudb.DatabaseValueMatchers.isSet;
import static com.github.tonivade.resp.protocol.RedisToken.integer;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(SetAddCommand.class)
public class SetAddCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void addOneValue()  {
    rule.withParams("key", "value")
      .execute()
      .assertValue("key", isSet("value"))
      .assertThat(integer(1));
  }

  @Test
  public void addMultipleValues()  {
    rule.withParams("key", "value1", "value2", "value3")
      .execute()
      .assertValue("key", isSet("value1", "value2", "value3"))
      .assertThat(integer(3));
  }
}
