/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;
import static tonivade.db.data.DatabaseValue.list;
import static tonivade.db.data.DatabaseValue.score;
import static tonivade.db.data.DatabaseValue.set;
import static tonivade.db.data.DatabaseValue.string;
import static tonivade.db.data.DatabaseValue.zset;
import static tonivade.db.persistence.HexUtil.toByteArray;

import java.io.InputStream;
import java.util.Map;

import org.junit.Test;

import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

public class RDBInputStreamTest {

    @Test
    public void testEmpty() throws Exception {
        RDBInputStream in = new RDBInputStream(array("524544495330303033FE00FF77DE0394AC9D23EA"));

        Map<Integer, IDatabase> databases = in.parse();

        assertThat(databases.size(), is(1));
    }

    @Test
    public void testAll() throws Exception {
        RDBInputStream in = new RDBInputStream(array("524544495330303033FE000001610474657374FE01010161010474657374FE02020161010474657374FE0303016101047465737403312E30FE040401610101310474657374FFE5C54809420836EA"));

        Map<Integer, IDatabase> databases = in.parse();

        assertThat(databases.size(), is(5));

        assertDB(databases.get(0), string("test"));
        assertDB(databases.get(1), list("test"));
        assertDB(databases.get(2), set("test"));
        assertDB(databases.get(3), zset(score(1.0, "test")));
        assertDB(databases.get(4), hash(entry("1", "test")));
    }

    private void assertDB(IDatabase db,DatabaseValue value) {
        assertThat(db, notNullValue());
        assertThat(db.get("a"), is(value));
    }

    private InputStream array(String string) {
        return new ByteBufferInputStream(toByteArray(string));
    }

}
