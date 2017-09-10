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

  DatabaseValue get(DatabaseKey key);

  DatabaseValue put(DatabaseKey key, DatabaseValue value);

  DatabaseValue remove(DatabaseKey key);

  void putAll(Map<? extends DatabaseKey, ? extends DatabaseValue> m);

  void clear();

  Set<DatabaseKey> keySet();

  Collection<DatabaseValue> values();

  Set<Map.Entry<DatabaseKey, DatabaseValue>> entrySet();

  default DatabaseValue putIfAbsent(DatabaseKey key, DatabaseValue value) {
    DatabaseValue oldValue = get(key);
    if (oldValue == null) {
        oldValue = put(key, value);
    }
    return oldValue;
  }

  default DatabaseValue merge(DatabaseKey key, DatabaseValue value,
      BiFunction<DatabaseValue, DatabaseValue, DatabaseValue> remappingFunction) {
    DatabaseValue oldValue = get(key);
    DatabaseValue newValue = oldValue == null ? value : remappingFunction.apply(oldValue, value);
    if(newValue == null) {
      remove(key);
    } else {
      put(key, newValue);
    }
    return newValue;
  }

  default DatabaseValue getOrDefault(DatabaseKey key, DatabaseValue defaultValue) {
    DatabaseValue value = get(key);
    return (value != null || containsKey(key))
        ? value
        : defaultValue;
  }

  default boolean isType(DatabaseKey key, DataType type) {
    DatabaseValue value = get(key);
    return value != null ? value.getType() == type : true;
  }

  default boolean rename(DatabaseKey from, DatabaseKey to) {
    DatabaseValue value = remove(from);
    if (value != null) {
      put(to, value);
      return true;
    }
    return false;
  }

  default void overrideAll(Map<DatabaseKey, DatabaseValue> value) {
    clear();
    putAll(value);
  }
}