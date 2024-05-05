/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

class OnHeapMVDatabaseTest extends DatabaseTest {

  public OnHeapMVDatabaseTest() {
    super(new OnHeapMVDatabaseFactory().create("db"));
  }
}
