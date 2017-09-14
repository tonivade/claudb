/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.data;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;

public class OnHeapDatabaseTest {

  private final Database database = new OnHeapDatabaseFactory().create("test");

  @Test
  public void testDatabase()  {
    database.put(safeKey("a"), string("value"));

    assertThat(database.get(safeKey("a")).getValue(), is(safeString("value")));
    assertThat(database.containsKey(safeKey("a")), is(true));
    assertThat(database.containsKey(safeKey("b")), is(false));
    assertThat(database.isEmpty(), is(false));
    assertThat(database.size(), is(1));

    Seq<DatabaseValue> values = database.values();

    assertThat(values.size(), is(1));
    assertThat(values.contains(string("value")), is(true));

    Set<DatabaseKey> keySet = database.keySet();

    assertThat(keySet.size(), is(1));
    assertThat(keySet.contains(safeKey("a")), is(true));

    Set<Tuple2<DatabaseKey, DatabaseValue>> entrySet = database.entrySet();

    assertThat(entrySet.size(), is(1));

    Tuple2<DatabaseKey, DatabaseValue> entry = entrySet.iterator().next();

    assertThat(entry._1(), is(safeKey("a")));
    assertThat(entry._2(), is(string("value")));
  }
}
