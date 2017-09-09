/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.data;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public interface Database {

  int size();

  boolean isEmpty();

  boolean containsKey(DatabaseKey key);

  boolean containsValue(DatabaseValue value);

  DatabaseValue get(DatabaseKey key);

  DatabaseValue put(DatabaseKey key, DatabaseValue value);

  DatabaseValue remove(DatabaseKey key);

  void putAll(Map<? extends DatabaseKey, ? extends DatabaseValue> m);

  void clear();

  Set<DatabaseKey> keySet();

  Collection<DatabaseValue> values();

  Set<Map.Entry<DatabaseKey, DatabaseValue>> entrySet();

  DatabaseValue putIfAbsent(DatabaseKey key, DatabaseValue value);

  DatabaseValue merge(DatabaseKey key, DatabaseValue value,
      BiFunction<? super DatabaseValue, ? super DatabaseValue, ? extends DatabaseValue> remappingFunction);

  boolean isType(DatabaseKey key, DataType type);

  boolean rename(DatabaseKey from, DatabaseKey to);
  
  DatabaseValue getOrDefault(DatabaseKey key, DatabaseValue defaultValue);

}