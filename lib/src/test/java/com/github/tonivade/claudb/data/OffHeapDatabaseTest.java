/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.junit.Before;
import org.junit.Test;
import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple2;

public class OffHeapDatabaseTest {

  private Database database = new OffHeapDatabaseFactory().create("test");

  @Before
  public void setUp() {
    database.clear();
  }

  @Test
  public void keySet() {
    database.put(safeKey("a"), string("1"));
    database.put(safeKey("b"), string("2"));
    database.put(safeKey("c"), string("3"));

    assertThat(database.keySet(), containsInAnyOrder(safeKey("a"), safeKey("b"), safeKey("c")));
  }

  @Test
  public void values() {
    database.put(safeKey("a"), string("1"));
    database.put(safeKey("b"), string("2"));
    database.put(safeKey("c"), string("3"));

    assertThat(database.values(), containsInAnyOrder(string("1"), string("2"), string("3")));
  }

  @Test
  public void entrySet() {
    database.put(safeKey("a"), string("1"));
    database.put(safeKey("b"), string("2"));
    database.put(safeKey("c"), string("3"));

    assertThat(database.entrySet(), containsInAnyOrder(entry(safeKey("a"), string("1")),
                                                       entry(safeKey("b"), string("2")),
                                                       entry(safeKey("c"), string("3"))));
  }

  private Tuple2<DatabaseKey, DatabaseValue> entry(DatabaseKey key, DatabaseValue value) {
    return Tuple.of(key, value);
  }
}
