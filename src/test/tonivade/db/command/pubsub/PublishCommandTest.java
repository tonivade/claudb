/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static tonivade.db.DatabaseValueMatchers.set;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.protocol.RedisToken;

import tonivade.db.ITinyDB;
import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

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
