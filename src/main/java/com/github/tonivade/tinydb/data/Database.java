/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.data;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;

import java.time.Instant;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.function.BiFunction;

import com.github.tonivade.resp.protocol.SafeString;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;

public interface Database {

  int size();

  boolean isEmpty();

  boolean containsKey(DatabaseKey key);

  DatabaseValue get(DatabaseKey key);

  DatabaseValue put(DatabaseKey key, DatabaseValue value);

  DatabaseValue remove(DatabaseKey key);

  void clear();

  Set<DatabaseKey> keySet();

  Seq<DatabaseValue> values();

  Set<Tuple2<DatabaseKey, DatabaseValue>> entrySet();

  default SafeString getString(SafeString key) {
    return getOrDefault(safeKey(key), DatabaseValue.EMPTY_STRING).getString();
  }

  default List<SafeString> getList(SafeString key) {
    return getOrDefault(safeKey(key), DatabaseValue.EMPTY_LIST).getList();
  }

  default Set<SafeString> getSet(SafeString key) {
    return getOrDefault(safeKey(key), DatabaseValue.EMPTY_SET).getSet();
  }

  default NavigableSet<Entry<Double, SafeString>> getSortedSet(SafeString key) {
    return getOrDefault(safeKey(key), DatabaseValue.EMPTY_ZSET).getSortedSet();
  }

  default Map<SafeString, SafeString> getHash(SafeString key) {
    return getOrDefault(safeKey(key), DatabaseValue.EMPTY_HASH).getHash();
  }

  default void putAll(Map<? extends DatabaseKey, ? extends DatabaseValue> map) {
    map.forEach(this::put);
  }

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

  default Set<DatabaseKey> evictableKeys(Instant now) {
    return entrySet()
        .filter(entry -> entry._2().isExpired(now))
        .map(Tuple2::_1).toSet();
  }
}