/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import org.h2.mvstore.MVStore;

public class OnHeapMVDatabaseFactory implements DatabaseFactory {

  private static final MVDatabase.DatabaseBuilder BUILDER = new MVDatabase.DatabaseBuilder();

  private final MVStore store;
  
  public OnHeapMVDatabaseFactory() {
    store = new MVStore.Builder().open();
  }

  @Override
  public Database create(String name) {
    return new MVDatabase(store.openMap(name, BUILDER));
  }

  @Override
  public void clear() {
    store.close();
  }
}
