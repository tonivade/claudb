/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

class OnHeapMVDatabaseTest extends DatabaseTest {

  public OnHeapMVDatabaseTest() {
    super(new OnHeapMVDatabaseFactory().create("db"));
  }
}
