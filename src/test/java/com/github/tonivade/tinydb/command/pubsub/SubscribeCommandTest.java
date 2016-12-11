/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.pubsub;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.isSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;
import com.github.tonivade.tinydb.command.pubsub.SubscribeCommand;

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
