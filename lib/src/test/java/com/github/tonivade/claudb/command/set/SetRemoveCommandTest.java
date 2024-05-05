/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.set;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.claudb.DatabaseValueMatchers.isSet;
import static com.github.tonivade.claudb.DatabaseValueMatchers.set;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(SetRemoveCommand.class)
public class SetRemoveCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.withData("key", set("a", "b", "c"))
        .withParams("key", "a")
        .execute()
        .assertValue("key", isSet("b", "c"))
        .assertThat(integer(1));

    rule.withParams("key", "a")
        .execute()
        .assertValue("key", isSet("b", "c"))
        .assertThat(integer(0));
  }
}
