/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static tonivade.db.DatabaseValueMatchers.set;
import static tonivade.redis.protocol.SafeString.safeString;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.ITinyDB;
import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.redis.command.IResponse;
import tonivade.redis.protocol.RedisToken;
import tonivade.redis.protocol.RedisToken.ArrayRedisToken;
import tonivade.redis.protocol.RedisToken.StringRedisToken;

@CommandUnderTest(PublishCommand.class)
public class PublishCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<ArrayRedisToken> captor;

    private List<RedisToken> expected = asList(new StringRedisToken(safeString("message")),
            new StringRedisToken(safeString("test")), new StringRedisToken(safeString("Hello World!")));

    @Test
    public void testExecute() throws Exception {
        rule.withAdminData("subscriptions:test", set("localhost:12345"))
            .withParams("test", "Hello World!")
            .execute();
        rule.verify(ITinyDB.class).publish(eq("localhost:12345"), captor.capture());
        rule.verify(IResponse.class).addInt(1);

        ArrayRedisToken array = captor.getValue();

        assertThat(array.<List<RedisToken>>getValue(), equalTo(expected));
    }

}
