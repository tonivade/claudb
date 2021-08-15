/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.Sequence;

public class OnHeapDatabaseTest {

  private final Database database = new OnHeapDatabaseFactory().create("test");

  @Test
  public void testDatabase()  {
    database.put(safeKey("a"), string("value"));

    assertThat(database.get(safeKey("a")).getString(), is(safeString("value")));
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
}
