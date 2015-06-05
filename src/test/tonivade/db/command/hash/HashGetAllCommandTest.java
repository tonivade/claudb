/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.hash;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyCollectionOf;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.command.hash.HashGetAllCommand;
import tonivade.db.data.DatabaseValue;

@CommandUnderTest(HashGetAllCommand.class)
public class HashGetAllCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<DatabaseValue> captor;

    @Test
    public void testExecute() {
        rule.withData("a",
                hash(entry("key1", "value1"),
                     entry("key2", "value2"),
                     entry("key3", "value3")))
            .withParams("a")
            .execute()
            .verify().addValue(captor.capture());

        DatabaseValue value = captor.getValue();

        Map<String, String> map = value.getValue();

        assertThat(map.get("key1"), is("value1"));
        assertThat(map.get("key2"), is("value2"));
        assertThat(map.get("key3"), is("value3"));
    }

    @Test
    public void testExecuteNotExists() {
        rule.withParams("a")
            .execute()
            .verify().addArray(anyCollectionOf(String.class));
    }

}
