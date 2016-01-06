/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.DatabaseValueMatchers.list;
import static tonivade.redis.protocol.SafeString.safeString;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.redis.protocol.SafeString;

@CommandUnderTest(ListRangeCommand.class)
public class ListRangeCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<SafeString>> captor;

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", list("a", "b", "c"))
            .withParams("key", "0", "-1")
            .execute()
            .verify().addArray(captor.capture());

        Collection<SafeString> result = captor.getValue();

        assertThat(result.size(), is(3));

        Iterator<SafeString> iter = result.iterator();

        assertThat(iter.next(), is(safeString("a")));
        assertThat(iter.next(), is(safeString("b")));
        assertThat(iter.next(), is(safeString("c")));
    }

}
