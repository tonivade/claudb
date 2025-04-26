/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import org.h2.mvstore.MVStore;

public class PersistentMVDatabaseFactory implements DatabaseFactory {

  private static final MVDatabase.DatabaseBuilder BUILDER = new MVDatabase.DatabaseBuilder();

  private final MVStore store;

  public PersistentMVDatabaseFactory(String fileName, int cacheConcurrency) {
    store = new MVStore.Builder()
        .fileName(fileName)
        .cacheConcurrency(cacheConcurrency)
        .open();
  }

  @Override
  public Database create(String name) {
    return new MVDatabase(store.openMap(name, BUILDER));
  }

  @Override
  public void clear() {
    store.compactFile(10_000);
    
    store.close();
  }
}
