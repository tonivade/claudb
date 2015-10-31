/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.hash;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyCollectionOf;
import static tonivade.db.DatabaseValueMatchers.entry;
import static tonivade.db.data.DatabaseValue.hash;
import static tonivade.redis.protocol.SafeString.safeString;

import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.data.DatabaseValue;
import tonivade.redis.protocol.SafeString;

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

        Map<SafeString, SafeString> map = value.getValue();

        assertThat(map.get(safeString("key1")), is(safeString("value1")));
        assertThat(map.get(safeString("key2")), is(safeString("value2")));
        assertThat(map.get(safeString("key3")), is(safeString("value3")));
    }

    @Test
    public void testExecuteNotExists() {
        rule.withParams("a")
            .execute()
            .verify().addArray(anyCollectionOf(SafeString.class));
    }

}
