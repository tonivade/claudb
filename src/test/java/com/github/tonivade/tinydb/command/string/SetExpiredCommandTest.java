/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.string;

import static com.github.tonivade.tinydb.DatabaseValueMatchers.isNotExpired;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.notNullValue;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.nullValue;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(SetExpiredCommand.class)
public class SetExpiredCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() throws InterruptedException  {
    rule.withParams("a", "1", "1")
        .execute()
        .assertValue("a", is(string("1")))
        .assertThat(RedisToken.status("OK"));

    Thread.sleep(400);
    rule.assertValue("a", isNotExpired())
        .assertValue("a", notNullValue());

    Thread.sleep(800);
    rule.assertValue("a", nullValue());
  }

}
