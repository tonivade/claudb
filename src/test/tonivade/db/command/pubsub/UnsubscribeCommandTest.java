/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.pubsub;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.set;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.command.ISession;

@CommandUnderTest(UnsubscribeCommand.class)
public class UnsubscribeCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<?>> captor;

    @Test
    public void testExecute() throws Exception {
        rule.withData("subscriptions:test", set("localhost:12345"))
            .withParams("test")
            .execute()
            .assertThat("subscriptions:test", is(set()));

        rule.verify(ISession.class).removeSubscription("test");

        rule.verify().addArray(captor.capture());

        Collection<?> response = captor.getValue();

        assertThat(response.size(), is(3));

        Iterator<?> iter = response.iterator();

        assertThat(iter.next(), is("unsubscribe"));
        assertThat(iter.next(), is("test"));
        assertThat(iter.next(), is(0));
    }

}
