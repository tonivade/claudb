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

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.CommandRule;
import com.github.tonivade.tinydb.command.CommandUnderTest;

@CommandUnderTest(HashKeysCommand.class)
public class HashKeysCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<Collection<SafeString>> captor;

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", hash(entry("a", "1"), entry("b", "2")))
            .withParams("key", "a")
            .execute()
            .verify().addSafeArray(captor.capture());

        Collection<SafeString> keys = captor.getValue();

        assertThat(keys.size(), is(2));
        assertThat(keys.contains(safeString("a")), is(true));
        assertThat(keys.contains(safeString("b")), is(true));
    }

}
