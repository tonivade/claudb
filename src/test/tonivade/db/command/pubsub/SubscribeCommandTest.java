/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static tonivade.db.DatabaseValueMatchers.isSet;
import static tonivade.redis.protocol.SafeString.safeString;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(SubscribeCommand.class)
public class SubscribeCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<?>> captor;

    @Test
    public void testExecute() throws Exception {
        rule.withParams("test")
            .execute()
            .assertAdminValue("subscriptions:test", isSet("localhost:12345"));

        assertThat(rule.getSessionState().getSubscriptions(), contains(safeString("test")));

        rule.verify().addArray(captor.capture());

        Collection<?> response = captor.getValue();

        assertThat(response.size(), is(3));

        Iterator<?> iter = response.iterator();

        assertThat(iter.next(), is(safeString("subscribe")));
        assertThat(iter.next(), is(safeString("test")));
        assertThat(iter.next(), is(1));
    }

}
