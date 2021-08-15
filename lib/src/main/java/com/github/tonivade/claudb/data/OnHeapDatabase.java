/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.purefun.data.Sequence;

import java.time.Instant;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class OnHeapDatabase implements Database {

  private final Map<DatabaseKey, DatabaseValue> cache;

  public OnHeapDatabase(Map<DatabaseKey, DatabaseValue> cache) {
    this.cache = requireNonNull(cache);
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
  public ImmutableSet<DatabaseKey> keySet() {
    return ImmutableSet.from(cache.keySet());
  }

  @Override
  public Sequence<DatabaseValue> values() {
    return ImmutableSet.from(cache.values());
  }

  @Override
  public ImmutableSet<Tuple2<DatabaseKey, DatabaseValue>> entrySet() {
    return ImmutableSet.from(cache.entrySet()).map(Tuple::from);
  }
}
