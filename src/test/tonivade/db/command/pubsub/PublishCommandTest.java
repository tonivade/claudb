/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static tonivade.db.DatabaseValueMatchers.set;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.ITinyDB;
import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.redis.command.IResponse;

@CommandUnderTest(PublishCommand.class)
public class PublishCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("subscriptions:test", set("localhost:12345"))
            .withParams("test", "Hello World!")
            .execute();
        rule.verify(ITinyDB.class).publish("localhost:12345", "*3\r\n$7\r\nmessage\r\n$4\r\ntest\r\n$12\r\nHello World!\r\n");
        rule.verify(IResponse.class).addInt(1);

    }

}
