/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.hash;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.DatabaseValueMatchers.entry;
import static tonivade.db.data.DatabaseValue.hash;
import static tonivade.redis.protocol.SafeString.safeString;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.redis.protocol.SafeString;

@CommandUnderTest(HashValuesCommand.class)
public class HashValuesCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<SafeString>> captor;

    @Test
    public void testExecute() {
        rule.withData("test",
                hash(entry("key1", "value1"),
                     entry("key2", "value2"),
                     entry("key3", "value3")))
            .withParams("test")
            .execute()
            .verify().addArray(captor.capture());

        Collection<SafeString> values = captor.getValue();

        assertThat(values.size(), is(3));
        assertThat(values.contains(safeString("value1")), is(true));
        assertThat(values.contains(safeString("value2")), is(true));
        assertThat(values.contains(safeString("value3")), is(true));
    }

    @Test
    public void testExecuteNotExists() {
        rule.withParams("test")
            .execute()
            .verify().addArray(captor.capture());

        Collection<SafeString> values = captor.getValue();

        assertThat(values.isEmpty(), is(true));
    }

}
