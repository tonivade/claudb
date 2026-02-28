/*
 * Copyright (c) 2015-2026, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

class OffHeapMVDatabaseTest extends DatabaseTest {

  public OffHeapMVDatabaseTest() {
    super(new OffHeapMVDatabaseFactory(2).create("db"));
  }
}
