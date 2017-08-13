package com.github.tonivade.tinydb.data;

public interface DatabaseFactory {
  Database create(String name);
  void clear();
}
