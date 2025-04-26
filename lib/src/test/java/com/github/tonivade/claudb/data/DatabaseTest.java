/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.junit.jupiter.api.Test;

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

    Collection<DatabaseValue> values = database.values();

    assertThat(values.size(), is(1));
    assertThat(values.contains(string("value")), is(true));

    Collection<DatabaseKey> keySet = database.keySet();

    assertThat(keySet.size(), is(1));
    assertThat(keySet.contains(safeKey("a")), is(true));

    Collection<Map.Entry<DatabaseKey, DatabaseValue>> entrySet = database.entrySet();

    assertThat(entrySet.size(), is(1));

    Map.Entry<DatabaseKey, DatabaseValue> entry = entrySet.iterator().next();

    assertThat(entry.getKey(), is(safeKey("a")));
    assertThat(entry.getValue(), is(string("value")));
  }

  @Test
  void isType() {
    database.put(safeKey("key"), string("key"));

    assertThat(database.isType(safeKey("key"), DataType.STRING), is(true));
    assertThat(database.isType(safeKey("key"), DataType.LIST), is(false));
    assertThat(database.isType(safeKey("any"), DataType.STRING), is(true));
  }
}
