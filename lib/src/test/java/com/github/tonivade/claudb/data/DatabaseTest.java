/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.Sequence;

abstract class DatabaseTest {

  private final Database database;

  protected DatabaseTest(Database database) {
    this.database = database;
  }

  @Test
  void testDatabase() throws IOException {
    database.put(safeKey("a"), string("value"));

    assertThat(database.get(safeKey("a")), is(string("value")));
    assertThat(database.getString(safeString("a")), is(safeString("value")));
    assertThat(database.containsKey(safeKey("a")), is(true));
    assertThat(database.containsKey(safeKey("b")), is(false));
    assertThat(database.isEmpty(), is(false));
    assertThat(database.size(), is(1));

    Sequence<DatabaseValue> values = database.values();

    assertThat(values.size(), is(1));
    assertThat(values.contains(string("value")), is(true));

    Sequence<DatabaseKey> keySet = database.keySet();

    assertThat(keySet.size(), is(1));
    assertThat(keySet.contains(safeKey("a")), is(true));

    Sequence<Tuple2<DatabaseKey, DatabaseValue>> entrySet = database.entrySet();

    assertThat(entrySet.size(), is(1));

    Tuple2<DatabaseKey, DatabaseValue> entry = entrySet.iterator().next();

    assertThat(entry.get1(), is(safeKey("a")));
    assertThat(entry.get2(), is(string("value")));
  }

  @Test
  void isType() {
    Database database = new OffHeapMVDatabaseFactory().create("db1");

    database.put(safeKey("key"), string("key"));

    assertThat(database.isType(safeKey("key"), DataType.STRING), is(true));
    assertThat(database.isType(safeKey("key"), DataType.LIST), is(false));
    assertThat(database.isType(safeKey("any"), DataType.STRING), is(true));
  }
}
