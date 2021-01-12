/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.claudb.DatabaseValueMatchers.isSet;
import static com.github.tonivade.claudb.data.DatabaseValue.set;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(PatternSubscribeCommand.class)
public class PatternSubscribeCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute() {
    rule.withParams("test:*")
        .execute()
        .assertThat(array(string("psubscribe"), string("test:*"), integer(1)))
        .assertAdminValue("psubscription:test:*", isSet("localhost:12345"));

    assertThat(rule.getSessionState().getSubscriptions(), contains(safeString("test:*")));
  }
  
  @Test
  public void testExecuteExisting() {
    rule.withAdminData("psubscription:test:*", set(safeString("localhost:54321")))
        .withParams("test:*")
        .execute()
        .assertThat(array(string("psubscribe"), string("test:*"), integer(1)))
        .assertAdminValue("psubscription:test:*", isSet("localhost:12345", "localhost:54321"));

    assertThat(rule.getSessionState().getSubscriptions(), contains(safeString("test:*")));
  }
  
  @Test
  public void testExecuteOther() {
    rule.getSessionState().addSubscription(safeString("other:*"));
    
    rule.withParams("test:*")
        .execute()
        .assertThat(array(string("psubscribe"), string("test:*"), integer(2)))
        .assertAdminValue("psubscription:test:*", isSet("localhost:12345"));

    assertThat(rule.getSessionState().getSubscriptions(), containsInAnyOrder(safeString("test:*"), 
                                                                             safeString("other:*")));
  }

}
