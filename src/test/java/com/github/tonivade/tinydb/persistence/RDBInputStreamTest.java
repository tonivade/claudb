/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.persistence;

import static com.github.tonivade.tinydb.DatabaseKeyMatchers.safeKey;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.entry;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.list;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.score;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.set;
import static com.github.tonivade.tinydb.data.DatabaseValue.hash;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;
import static com.github.tonivade.tinydb.data.DatabaseValue.zset;
import static com.github.tonivade.tinydb.persistence.HexUtil.toByteArray;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.Map;

import org.junit.Test;

import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.IDatabase;

public class RDBInputStreamTest {

    @Test
    public void testEmpty() throws Exception {
        RDBInputStream in = new RDBInputStream(array("524544495330303033FE00FF77DE0394AC9D23EA"));

        Map<Integer, IDatabase> databases = in.parse();

        assertThat(databases.size(), is(1));
    }

    @Test
    public void testAll() throws Exception {
        RDBInputStream in = new RDBInputStream(array("524544495330303033FE000001610474657374FE01010161010474657374FE02020161010474657374FE0303016101047465737403312E30FE040401610101310474657374FE05FC00000000000000010001610474657374FFA9D1F09C463A7043"));

        Map<Integer, IDatabase> databases = in.parse();

        assertThat(databases.size(), is(6));

        assertDB(databases.get(0), string("test"));
        assertDB(databases.get(1), list("test"));
        assertDB(databases.get(2), set("test"));
        assertDB(databases.get(3), zset(score(1.0, "test")));
        assertDB(databases.get(4), hash(entry("1", "test")));
        assertThat(databases.get(5), notNullValue());
        assertThat(databases.get(5).isEmpty(), is(true));
    }

    private void assertDB(IDatabase db,DatabaseValue value) {
        assertThat(db, notNullValue());
        assertThat(db.get(safeKey("a")), is(value));
    }

    private InputStream array(String string) {
        return new ByteBufferInputStream(toByteArray(string));
    }

}
