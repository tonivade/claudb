/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.data;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class OnHeapDatabase implements Database {

  private final Map<DatabaseKey, DatabaseValue> cache;

  public OnHeapDatabase() {
    this(new HashMap<>());
  }

  public OnHeapDatabase(Map<DatabaseKey, DatabaseValue> cache) {
    this.cache = cache;
  }

  @Override
  public int size() {
    return cache.size();
  }

  @Override
  public boolean isEmpty() {
    return cache.isEmpty();
  }

  @Override
  public boolean containsKey(DatabaseKey key) {
    return cache.containsKey(key);
  }

  @Override
  public boolean containsValue(DatabaseValue value) {
    return cache.containsValue(value);
  }

  @Override
  public DatabaseValue get(DatabaseKey key) {
    DatabaseValue value = cache.get(key);

    if (value != null) {
      if (!value.isExpired(Instant.now())) {
        return value;
      }

      cache.remove(key);
    }

    return null;
  }

  @Override
  public DatabaseValue put(DatabaseKey key, DatabaseValue value) {
    DatabaseValue oldValue = cache.remove(key);
    cache.put(key, value);
    return oldValue;
  }

  @Override
  public DatabaseValue remove(DatabaseKey key) {
    return cache.remove(key);
  }

  @Override
  public void putAll(Map<? extends DatabaseKey, ? extends DatabaseValue> m) {
    cache.putAll(m);
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public Set<DatabaseKey> keySet() {
    return unmodifiableSet(cache.keySet().stream().collect(toSet()));
  }

  @Override
  public Collection<DatabaseValue> values() {
    return unmodifiableList(cache.values().stream().collect(toList()));
  }

  @Override
  public Set<Map.Entry<DatabaseKey, DatabaseValue>> entrySet() {
    return cache.entrySet().stream().map(entry -> new SimpleEntry<>(entry.getKey(), entry.getValue())).collect(toSet());
  }

  @Override
  public DatabaseValue putIfAbsent(DatabaseKey key, DatabaseValue value) {
    return cache.putIfAbsent(key, value);
  }

  @Override
  public DatabaseValue merge(DatabaseKey key, DatabaseValue value,
      BiFunction<? super DatabaseValue, ? super DatabaseValue, ? extends DatabaseValue> remappingFunction) {
    return cache.merge(key, value, remappingFunction);
  }

  @Override
  public boolean isType(DatabaseKey key, DataType type) {
    return cache.getOrDefault(key, new DatabaseValue(type)).getType() == type;
  }

  @Override
  public boolean rename(DatabaseKey from, DatabaseKey to) {
    DatabaseValue value = cache.remove(from);
    if (value != null) {
      cache.put(to, value);
      return true;
    }
    return false;
  }
  
  @Override
  public DatabaseValue getOrDefault(DatabaseKey key, DatabaseValue defaultValue) {
    return cache.getOrDefault(key, defaultValue);
  }
}
