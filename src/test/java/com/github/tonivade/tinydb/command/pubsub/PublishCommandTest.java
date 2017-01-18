/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.set;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.ITinyDB;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.pubsub.PublishCommand;

@CommandUnderTest(PublishCommand.class)
public class PublishCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<RedisToken> captor;

    private RedisToken expected = array(string("message"), string("test"), string("Hello World!"));

    @Test
    public void testExecute() throws Exception {
        rule.withAdminData("subscriptions:test", set("localhost:12345"))
            .withParams("test", "Hello World!")
            .execute();
        rule.verify(ITinyDB.class).publish(eq("localhost:12345"), captor.capture());
        rule.verify(IResponse.class).addInt(1);

        RedisToken array = captor.getValue();

        assertThat(array, equalTo(expected));
    }

}
