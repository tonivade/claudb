/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.claudb.DatabaseValueMatchers.set;

import org.junit.Rule;
import org.junit.Test;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.command.CommandRule;
import com.github.tonivade.claudb.command.CommandUnderTest;

@CommandUnderTest(PublishCommand.class)
public class PublishCommandTest {

  @Rule
  public final CommandRule rule = new CommandRule(this);

  @Test
  public void publishNoSubscriptions()  {
    rule.withParams("test", "Hello World!")
        .execute()
        .assertThat(RedisToken.integer(0));
  }

  @Test
  public void publish()  {
    rule.withAdminData("subscription:test", set("localhost:12345"))
        .withParams("test", "Hello World!")
        .execute()
        .assertThat(RedisToken.integer(1))
        .verify(DBServerContext.class).publish("localhost:12345",
            array(string("message"), string("test"), string("Hello World!")));
  }

  @Test
  public void publishPattern() {
    rule.withAdminData("psubscription:test:*", set("localhost:12345"))
        .withParams("test:pepe", "Hello World!")
        .execute()
        .assertThat(RedisToken.integer(1))
        .verify(DBServerContext.class).publish("localhost:12345",
             array(string("pmessage"), string("test:*"), string("test:pepe"), string("Hello World!")));
  }

  @Test
  public void publishBoth() {
    rule.withAdminData("subscription:test:pepe", set("localhost:12345"))
        .withAdminData("psubscription:test:*", set("localhost:54321"))
        .withParams("test:pepe", "Hello World!")
        .execute()
        .assertThat(RedisToken.integer(2));
    
    rule.verify(DBServerContext.class).publish("localhost:12345",
        array(string("message"), string("test:pepe"), string("Hello World!")));
    rule.verify(DBServerContext.class).publish("localhost:54321",
        array(string("pmessage"), string("test:*"), string("test:pepe"), string("Hello World!")));
  }
}
