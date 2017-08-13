/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.data;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.AbstractMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

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

  private Map.Entry<DatabaseKey, DatabaseValue> entry(DatabaseKey key, DatabaseValue value) {
    return new AbstractMap.SimpleEntry<>(key, value);
  }
}
