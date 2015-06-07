/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.list;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(ListRangeCommand.class)
public class ListRangeCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<String>> captor;

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", list("a", "b", "c"))
            .withParams("key", "0", "-1")
            .execute()
            .verify().addArray(captor.capture());

        Collection<String> result = captor.getValue();

        assertThat(result.size(), is(3));

        Iterator<String> iter = result.iterator();

        assertThat(iter.next(), is("a"));
        assertThat(iter.next(), is("b"));
        assertThat(iter.next(), is("c"));
    }

}
