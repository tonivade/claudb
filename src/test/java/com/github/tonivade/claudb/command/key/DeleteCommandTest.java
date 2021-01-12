/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.key;

import static com.github.tonivade.claudb.DatabaseValueMatchers.nullValue;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(DeleteCommand.class)
public class DeleteCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withData("test", string("value"))
    .withParams("test")
    .execute()
    .assertValue("test", is(nullValue()))
    .assertThat(RedisToken.integer(1));
  }

}
