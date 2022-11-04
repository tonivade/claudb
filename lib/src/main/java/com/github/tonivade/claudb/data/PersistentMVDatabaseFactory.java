/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import org.h2.mvstore.MVStore;

public class PersistentMVDatabaseFactory implements DatabaseFactory {

  private static final MVDatabase.DatabaseBuilder BUILDER = new MVDatabase.DatabaseBuilder();

  private final MVStore store = new MVStore.Builder().fileName("./claudb.db").open();

  @Override
  public Database create(String name) {
    return new MVDatabase(store.openMap(name, BUILDER));
  }

  @Override
  public void clear() {
    store.close();
  }
}
