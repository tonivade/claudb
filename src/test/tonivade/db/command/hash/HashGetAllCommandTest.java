/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.hash;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyCollectionOf;
import static tonivade.db.DatabaseValueMatchers.entry;
import static tonivade.db.data.DatabaseValue.hash;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.resp.protocol.SafeString;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(HashGetAllCommand.class)
public class HashGetAllCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<SafeString>> captor;

    @Test
    public void testExecute() {
        rule.withData("a",
                hash(entry("key1", "value1"),
                     entry("key2", "value2"),
                     entry("key3", "value3")))
            .withParams("a")
            .execute()
            .verify().addArray(captor.capture());

        Collection<SafeString> value = captor.getValue();

        Iterator<SafeString> i = value.iterator();

        assertThat(i.next(), is(safeString("key1")));
        assertThat(i.next(), is(safeString("value1")));
        assertThat(i.next(), is(safeString("key3")));
        assertThat(i.next(), is(safeString("value3")));
        assertThat(i.next(), is(safeString("key2")));
        assertThat(i.next(), is(safeString("value2")));
    }

    @Test
    public void testExecuteNotExists() {
        rule.withParams("a")
            .execute()
            .verify().addArray(anyCollectionOf(SafeString.class));
    }

}
