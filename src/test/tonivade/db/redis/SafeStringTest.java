/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.redis;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.persistence.HexUtil.toHexString;
import static tonivade.db.redis.SafeString.safeAsList;
import static tonivade.db.redis.SafeString.safeString;

import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class SafeStringTest {

    @Test
    public void testBytes() throws Exception {
        SafeString str = safeString("Hola Mundo!");

        assertThat(new SafeString(str.getBuffer()), is(str));
        assertThat(str.length(), is(11));
        assertThat(toHexString(str.getBytes()), is("486F6C61204D756E646F21"));
        assertThat(str.toString(), is("Hola Mundo!"));
    }

    @Test
    public void testList() throws Exception {
        List<SafeString> list = safeAsList("1", "2", "3");

        assertThat(list.size(), is(3));
        assertThat(list.get(0), is(safeString("1")));
        assertThat(list.get(1), is(safeString("2")));
        assertThat(list.get(2), is(safeString("3")));
    }

    @Test
    public void testSet() throws Exception {
        NavigableSet<SafeString> set = new TreeSet<>(safeAsList("1", "2", "3"));

        SortedSet<SafeString> result = set.subSet(safeString("2"), safeString("4"));

        assertThat(result.size(), is(2));
        Iterator<SafeString> iterator = result.iterator();
        assertThat(iterator.next(), is(safeString("2")));
        assertThat(iterator.next(), is(safeString("3")));
    }

}
