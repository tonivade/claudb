package com.github.tonivade.claudb.data;

public class H2DatabaseFactory implements DatabaseFactory {

  @Override
  public Database create(String name) {
    return new H2Database("/tmp/" + name + ".db");
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub
  }
}
