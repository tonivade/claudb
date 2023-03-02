/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import org.h2.mvstore.MVStore;
import org.h2.mvstore.OffHeapStore;

public class OffHeapMVDatabaseFactory implements DatabaseFactory {

  private static final MVDatabase.DatabaseBuilder BUILDER = new MVDatabase.DatabaseBuilder();

  private final MVStore store;
  
  public OffHeapMVDatabaseFactory(int cacheConcurrency) {
    store = new MVStore.Builder()
        .adoptFileStore(new OffHeapStore())
        .cacheConcurrency(cacheConcurrency)
        .open();
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
