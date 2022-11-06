/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.claudb.data.DatabaseValue.list;
import static com.github.tonivade.claudb.data.DatabaseValue.set;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import java.time.Instant;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.function.BinaryOperator;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.ImmutableMap;
import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.resp.protocol.SafeString;

public interface Database {

  int size();

  default boolean isEmpty() {
    return size() == 0;
  }

  boolean containsKey(DatabaseKey key);

  DatabaseValue get(DatabaseKey key);

  default DatabaseValue get(SafeString key) {
    return get(safeKey(key));
  }

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

  default DatabaseValue get(String key) {
    return get(safeKey(key));
  }

  default SafeString getString(String key) {
    return getString(safeString(key));
  }

  default ImmutableList<SafeString> getList(String key) {
    return getList(safeString(key));
  }

  default ImmutableSet<SafeString> getSet(String key) {
    return getSet(safeString(key));
  }

  default ImmutableMap<SafeString ,SafeString> getHash(String key) {
    return getHash(safeString(key));
  }

  default NavigableSet<Entry<Double, SafeString>> getSortedSet(String key) {
    return getSortedSet(safeString(key));
  }

  DatabaseValue put(DatabaseKey key, DatabaseValue value);

  default DatabaseValue put(SafeString key, DatabaseValue value) {
    return put(safeKey(key), value);
  }

  default DatabaseValue putString(SafeString key, String value) {
    return put(safeKey(key), string(value));
  }

  default DatabaseValue putList(SafeString key, ImmutableList<SafeString> value) {
    return put(safeKey(key), list(value));
  }

  default DatabaseValue putList(SafeString key, SafeString... values) {
    return put(safeKey(key), list(values));
  }

  default DatabaseValue putSet(SafeString key, ImmutableSet<SafeString> value) {
    return put(safeKey(key), set(value));
  }

  default DatabaseValue putSet(SafeString key, SafeString... values) {
    return put(safeKey(key), set(values));
  }

  default DatabaseValue putHash(SafeString key, ImmutableMap<SafeString, SafeString> value) {
    return put(safeKey(key), hash(value));
  }

  default DatabaseValue putSortedSet(SafeString key, NavigableSet<Entry<Double, SafeString>> value) {
    return put(safeKey(key), zset(value));
  }

  default DatabaseValue put(String key, DatabaseValue value) {
    return put(safeKey(key), value);
  }

  default DatabaseValue putString(String key, String value) {
    return putString(safeString(key), value);
  }

  default DatabaseValue putList(String key, ImmutableList<SafeString> value) {
    return putList(safeString(key), value);
  }

  default DatabaseValue putList(String key, SafeString... values) {
    return putList(safeString(key), values);
  }

  default DatabaseValue putSet(String key, ImmutableSet<SafeString> value) {
    return putSet(safeString(key), value);
  }

  default DatabaseValue putSet(String key, SafeString... values) {
    return putSet(safeString(key), values);
  }

  default DatabaseValue putHash(String key, ImmutableMap<SafeString, SafeString> value) {
    return putHash(safeString(key), value);
  }

  default DatabaseValue putSortedSet(String key, NavigableSet<Entry<Double, SafeString>> value) {
    return putSortedSet(safeString(key), value);
  }

  DatabaseValue remove(DatabaseKey key);

  default DatabaseValue remove(String key) {
    return remove(safeKey(key));
  }

  default DatabaseValue remove(SafeString key) {
    return remove(safeKey(key));
  }

  void clear();

  ImmutableSet<DatabaseKey> keySet();

  Sequence<DatabaseValue> values();

  ImmutableSet<Tuple2<DatabaseKey, DatabaseValue>> entrySet();

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
      BinaryOperator<DatabaseValue> remappingFunction) {
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
    if (value == null) {
      return true;
    }
    return value.getType() == type;
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
