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
import static java.util.stream.Collectors.toSet;
import com.github.tonivade.resp.protocol.SafeString;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.function.BinaryOperator;

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

  default DatabaseValue get(String key) {
    return get(safeKey(key));
  }

  default SafeString getString(String key) {
    return getString(safeString(key));
  }

  default List<SafeString> getList(String key) {
    return getList(safeString(key));
  }

  default Set<SafeString> getSet(String key) {
    return getSet(safeString(key));
  }

  default Map<SafeString ,SafeString> getHash(String key) {
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

  default DatabaseValue putList(SafeString key, List<SafeString> value) {
    return put(safeKey(key), list(value));
  }

  default DatabaseValue putList(SafeString key, SafeString... values) {
    return put(safeKey(key), list(values));
  }

  default DatabaseValue putSet(SafeString key, Set<SafeString> value) {
    return put(safeKey(key), set(value));
  }

  default DatabaseValue putSet(SafeString key, SafeString... values) {
    return put(safeKey(key), set(values));
  }

  default DatabaseValue putHash(SafeString key, Map<SafeString, SafeString> value) {
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

  default DatabaseValue putList(String key, List<SafeString> value) {
    return putList(safeString(key), value);
  }

  default DatabaseValue putList(String key, SafeString... values) {
    return putList(safeString(key), values);
  }

  default DatabaseValue putSet(String key, Set<SafeString> value) {
    return putSet(safeString(key), value);
  }

  default DatabaseValue putSet(String key, SafeString... values) {
    return putSet(safeString(key), values);
  }

  default DatabaseValue putHash(String key, Map<SafeString, SafeString> value) {
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

  Set<DatabaseKey> keySet();

  Collection<DatabaseValue> values();

  Set<Map.Entry<DatabaseKey, DatabaseValue>> entrySet();

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

  default void overrideAll(Map<DatabaseKey, DatabaseValue> value) {
    clear();
    putAll(value);
  }

  default Set<DatabaseKey> evictableKeys(Instant now) {
    return entrySet().stream()
        .filter(entry -> entry.getValue().isExpired(now))
        .map(Map.Entry::getKey)
        .collect(toSet());
  }
}
