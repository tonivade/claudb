/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.data;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.DatabaseKeyMatchers.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.SimpleDatabase;

public class DatabaseTest {

    private final SimpleDatabase database = new SimpleDatabase();

    @Test
    public void testDatabase() throws Exception {
        database.put(safeKey("a"), string("value"));

        assertThat(database.get(safeKey("a")).getValue(), is(safeString("value")));
        assertThat(database.containsKey(safeKey("a")), is(true));
        assertThat(database.containsKey(safeKey("b")), is(false));
        assertThat(database.isEmpty(), is(false));
        assertThat(database.size(), is(1));

        Collection<DatabaseValue> values = database.values();

        assertThat(values.size(), is(1));
        assertThat(values.contains(string("value")), is(true));

        Set<DatabaseKey> keySet = database.keySet();

        assertThat(keySet.size(), is(1));
        assertThat(keySet.contains(safeKey("a")), is(true));

        Set<Entry<DatabaseKey, DatabaseValue>> entrySet = database.entrySet();

        assertThat(entrySet.size(), is(1));

        Entry<DatabaseKey, DatabaseValue> entry = entrySet.iterator().next();

        assertThat(entry.getKey(), is(safeKey("a")));
        assertThat(entry.getValue(), is(string("value")));
    }

    @Test
    public void testExpire() throws Exception {
        database.put(safeKey("a"), string("1"));
        database.overrideKey(safeKey("a", 10));
        database.getKey(safeKey(""));

        DatabaseKey key = database.getKey(safeKey("a"));
        assertThat(key, is(notNullValue()));
        assertThat(key.expiredAt(), is(greaterThan(Instant.now())));
    }

}
