package com.github.tonivade.tinydb.data;

public class OnHeapDatabaseFactory implements DatabaseFactory {

  @Override
  public Database create(String name) {
    return new SimpleDatabase();
  }

  @Override
  public void clear() {
    // nothing to clear
  }
}
