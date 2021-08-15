/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;

import java.time.Instant;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.function.BiFunction;

import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.ImmutableMap;
import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.resp.protocol.SafeString;

public interface Database {

  int size();

  boolean isEmpty();

  boolean containsKey(DatabaseKey key);

  DatabaseValue get(DatabaseKey key);

  DatabaseValue put(DatabaseKey key, DatabaseValue value);

  DatabaseValue remove(DatabaseKey key);

  void clear();

  ImmutableSet<DatabaseKey> keySet();

  Sequence<DatabaseValue> values();

  ImmutableSet<Tuple2<DatabaseKey, DatabaseValue>> entrySet();

  default SafeString getString(SafeString key) {
    return getOrDefault(safeKey(key), DatabaseValue.EMPTY_STRING).getString();
  }

  default ImmutableList<SafeString> getList(SafeString key) {
    return getOrDefault(safeKey(key), DatabaseValue.EMPTY_LIST).getList();
  }

  default ImmutableSet<SafeString> getSet(SafeString key) {
    return getOrDefault(safeKey(key), DatabaseValue.EMPTY_SET).getSet();
  }

  default NavigableSet<Entry<Double, SafeString>> getSortedSet(SafeString key) {
    return getOrDefault(safeKey(key), DatabaseValue.EMPTY_ZSET).getSortedSet();
  }

  default ImmutableMap<SafeString, SafeString> getHash(SafeString key) {
    return getOrDefault(safeKey(key), DatabaseValue.EMPTY_HASH).getHash();
  }

  default void putAll(ImmutableMap<? extends DatabaseKey, ? extends DatabaseValue> map) {
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
    return (value != null || containsKey(key)) ? value : defaultValue;
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

  default void overrideAll(ImmutableMap<DatabaseKey, DatabaseValue> value) {
    clear();
    putAll(value);
  }

  default ImmutableSet<DatabaseKey> evictableKeys(Instant now) {
    return entrySet()
        .filter(entry -> entry.get2().isExpired(now))
        .map(Tuple2::get1);
  }
}
