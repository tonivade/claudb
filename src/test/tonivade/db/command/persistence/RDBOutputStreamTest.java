/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.command.persistence.HexUtil.toHexString;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;
import static tonivade.db.data.DatabaseValue.list;
import static tonivade.db.data.DatabaseValue.set;
import static tonivade.db.data.DatabaseValue.string;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import tonivade.db.data.Database;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.db.persistence.RDBOutputStream;

public class RDBOutputStreamTest {

    private ByteArrayOutputStream baos;
    private RDBOutputStream out;

    @Before
    public void setUp() throws Exception {
        baos = new ByteArrayOutputStream();
        out = new RDBOutputStream(baos);
    }

    @Test
    public void testStartEnd() throws Exception {
        out.preamble(3);
        out.end();

        assertThat(toHexString(baos.toByteArray()), is("52454449533030303033FF66A145766BC31005"));
    }

    @Test
    public void testString() throws Exception {
        out.select(0);
        out.dabatase(database().add("a", string("test")).build());

        assertThat(toHexString(baos.toByteArray()), is("FE000001610474657374"));
    }

    @Test
    public void testList() throws Exception {
        out.select(0);
        out.dabatase(database().add("a", list("test")).build());

        assertThat(toHexString(baos.toByteArray()), is("FE00010161010474657374"));
    }

    @Test
    public void testSet() throws Exception {
        out.select(0);
        out.dabatase(database().add("a", set("test")).build());

        assertThat(toHexString(baos.toByteArray()), is("FE00020161010474657374"));
    }

    @Test
    public void testHash() throws Exception {
        out.select(0);
        out.dabatase(database().add("a", hash(entry("1", "test"))).build());

        assertThat(toHexString(baos.toByteArray()), is("FE000401610101310474657374"));
    }

    public static DatabaseBuiler database() {
        return new DatabaseBuiler();
    }

    private static class DatabaseBuiler {

        private IDatabase db = new Database(new HashMap<String, DatabaseValue>());

        public DatabaseBuiler add(String key, DatabaseValue value) {
            db.put(key, value);
            return this;
        }

        public IDatabase build() {
            return db;
        }
    }

}
