/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.hash;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.entry;
import static com.github.tonivade.tinydb.data.DatabaseValue.hash;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

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
            .verify().addArray(null);
    }

}
