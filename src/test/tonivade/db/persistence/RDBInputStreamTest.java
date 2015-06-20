/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.persistence.HexUtil.toByteArray;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.junit.Test;

import tonivade.db.data.IDatabase;

public class RDBInputStreamTest {

    @Test
    public void test() throws Exception {
        RDBInputStream in = new RDBInputStream(array("524544495330303033FE00FF77DE0394AC9D23EA"));

        Map<Integer, IDatabase> databases = in.parse();

        assertThat(databases.size(), is(1));
    }

    private ByteArrayInputStream array(String string) {
        return new ByteArrayInputStream(toByteArray(string));
    }

}
