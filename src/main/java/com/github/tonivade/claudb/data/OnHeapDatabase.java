/*
 * Copyright (c) 2015-2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import java.time.Instant;
import java.util.Map;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;

public class OnHeapDatabase implements Database {

  private final Map<DatabaseKey, DatabaseValue> cache;

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
  public void clear() {
    cache.clear();
  }

  @Override
  public Set<DatabaseKey> keySet() {
    return HashSet.ofAll(cache.keySet());
  }

  @Override
  public Seq<DatabaseValue> values() {
    return List.ofAll(cache.values());
  }

  @Override
  public Set<Tuple2<DatabaseKey, DatabaseValue>> entrySet() {
    return HashSet.ofAll(cache.entrySet()).map(this::toTuple2);
  }

  private Tuple2<DatabaseKey, DatabaseValue> toTuple2(Map.Entry<DatabaseKey, DatabaseValue> entry) {
    return Tuple.of(entry.getKey(), entry.getValue());
  }
}
