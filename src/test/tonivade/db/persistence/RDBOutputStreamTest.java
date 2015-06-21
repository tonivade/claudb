/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;
import static tonivade.db.data.DatabaseValue.list;
import static tonivade.db.data.DatabaseValue.score;
import static tonivade.db.data.DatabaseValue.set;
import static tonivade.db.data.DatabaseValue.string;
import static tonivade.db.data.DatabaseValue.zset;
import static tonivade.db.persistence.HexUtil.toHexString;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import tonivade.db.data.Database;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

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
        out.select(0);
        out.end();

        assertThat(toHexString(baos.toByteArray()), is("524544495330303033FE00FF77DE0394AC9D23EA"));
    }

    @Test
    public void testString() throws Exception {
        out.dabatase(database().add("a", string("test")).build());

        assertThat(toHexString(baos.toByteArray()), is("0001610474657374"));
    }

    @Test
    public void testList() throws Exception {
        out.dabatase(database().add("a", list("test")).build());

        assertThat(toHexString(baos.toByteArray()), is("010161010474657374"));
    }

    @Test
    public void testSet() throws Exception {
        out.dabatase(database().add("a", set("test")).build());

        assertThat(toHexString(baos.toByteArray()), is("020161010474657374"));
    }

    @Test
    public void testSortedSet() throws Exception {
        out.dabatase(database().add("a", zset(score(1.0, "test"))).build());

        assertThat(toHexString(baos.toByteArray()), is("03016101047465737403312E30"));
    }

    @Test
    public void testHash() throws Exception {
        out.dabatase(database().add("a", hash(entry("1", "test"))).build());

        assertThat(toHexString(baos.toByteArray()), is("0401610101310474657374"));
    }

    @Test
    public void testAll() throws Exception {
        out.preamble(3);
        out.select(0);
        out.dabatase(database().add("a", string("test")).build());
        out.select(1);
        out.dabatase(database().add("a", list("test")).build());
        out.select(2);
        out.dabatase(database().add("a", set("test")).build());
        out.select(3);
        out.dabatase(database().add("a", zset(score(1.0, "test"))).build());
        out.select(4);
        out.dabatase(database().add("a", hash(entry("1", "test"))).build());
        out.end();

        assertThat(toHexString(baos.toByteArray()), is("524544495330303033FE000001610474657374FE01010161010474657374FE02020161010474657374FE0303016101047465737403312E30FE040401610101310474657374FFE5C54809420836EA"));
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
