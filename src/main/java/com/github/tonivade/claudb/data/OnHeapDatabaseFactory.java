/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import java.util.HashMap;

public class OnHeapDatabaseFactory implements DatabaseFactory {

  @Override
  public Database create(String name) {
    return new OnHeapDatabase(new HashMap<>());
  }

  @Override
  public void clear() {
    // nothing to clear
  }
}
