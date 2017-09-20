package com.github.tonivade.claudb.data;

public interface DatabaseFactory {
  Database create(String name);
  void clear();
}
