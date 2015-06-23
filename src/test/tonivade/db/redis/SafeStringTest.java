/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.redis;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.persistence.HexUtil.toHexString;

import java.util.List;

import org.junit.Test;

public class SafeStringTest {

    @Test
    public void testBytes() throws Exception {
        SafeString str = SafeString.safeString("Hola Mundo!");

        assertThat(new SafeString(str.getBuffer()), is(str));
        assertThat(str.length(), is(11));
        assertThat(toHexString(str.getBytes()), is("486F6C61204D756E646F21"));
        assertThat(str.toString(), is("Hola Mundo!"));
    }

    @Test
    public void testList() throws Exception {
        List<SafeString> list = SafeString.safeAsList("1", "2", "3");

        assertThat(list.size(), is(3));
        assertThat(list.get(0), is(SafeString.safeString("1")));
        assertThat(list.get(1), is(SafeString.safeString("2")));
        assertThat(list.get(2), is(SafeString.safeString("3")));
    }

}
