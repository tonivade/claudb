/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import org.h2.mvstore.MVStore;

public class H2DatabaseFactory implements DatabaseFactory {

  private static final H2Database.DatabaseBuilder BUILDER = new H2Database.DatabaseBuilder();
  
  private MVStore store = new MVStore.Builder().fileName("/tmp/claudb.db").open();

  @Override
  public Database create(String name) {
    return new H2Database(store.openMap(name, BUILDER));
  }

  @Override
  public void clear() {
    store.close();
  }
}
