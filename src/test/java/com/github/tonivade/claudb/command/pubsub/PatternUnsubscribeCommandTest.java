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
import static com.github.tonivade.claudb.DatabaseValueMatchers.set;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(PatternUnsubscribeCommand.class)
public class PatternUnsubscribeCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void testExecute()  {
    rule.getSessionState().addSubscription(safeString("test:*"));
    
    rule.withAdminData("psubscription:test:*", set("localhost:12345"))
        .withParams("test:*")
        .execute()
        .assertThat(array(string("punsubscribe"), string("test:*"), integer(0)))
        .assertAdminValue("psubscription:test:*", isSet());

    assertThat(rule.getSessionState().getSubscriptions(), not(contains(safeString("test:*"))));
  }

}
