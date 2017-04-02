/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.isSet;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.set;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(UnsubscribeCommand.class)
public class UnsubscribeCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Captor
  private ArgumentCaptor<Collection<?>> captor;

  @Test
  public void testExecute()  {
    rule.withAdminData("subscriptions:test", set("localhost:12345"))
    .withParams("test")
    .execute()
    .then(array(string("unsubscribe"), string("test"), integer(0)))
    .assertAdminValue("subscriptions:test", isSet());

    assertThat(rule.getSessionState().getSubscriptions(), not(contains(safeString("test"))));
  }

}
