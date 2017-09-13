/*
 * Copyright (c) 2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.data;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.caffinitas.ohc.CloseableIterator;
import org.caffinitas.ohc.OHCache;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;

public class OffHeapDatabase implements Database {

  private OHCache<DatabaseKey, DatabaseValue> cache;

  public OffHeapDatabase(OHCache<DatabaseKey, DatabaseValue> cache) {
    this.cache = cache;
  }

  @Override
  public int size() {
    return (int) cache.size();
  }

  @Override
  public boolean isEmpty() {
    return cache.size() == 0;
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
    cache.put(key, value);
    return value;
  }

  @Override
  public DatabaseValue remove(DatabaseKey key) {
    DatabaseValue value = cache.get(key);
    cache.remove(key);
    return value;
  }

  @Override
  public void putAll(Map<? extends DatabaseKey, ? extends DatabaseValue> m) {
    for (Map.Entry<? extends DatabaseKey, ? extends DatabaseValue> entry : m.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public Set<DatabaseKey> keySet() {
    HashSet<DatabaseKey> keys = new HashSet<>();
    try (CloseableIterator<DatabaseKey> iterator = cache.keyIterator()) {
      while(iterator.hasNext()) {
        keys.add(iterator.next());
      }
    } catch(IOException e) {
      throw new UncheckedIOException(e);
    }
    return io.vavr.collection.HashSet.ofAll(keys);
  }

  @Override
  public Seq<DatabaseValue> values() {
    LinkedList<DatabaseValue> values = new LinkedList<>();
    for (DatabaseKey key : keySet()) {
      values.add(cache.get(key));
    }
    return List.ofAll(values);
  }

  @Override
  public Set<Tuple2<DatabaseKey, DatabaseValue>> entrySet() {
    HashSet<Map.Entry<DatabaseKey, DatabaseValue>> entries = new HashSet<>();
    for (DatabaseKey key : keySet()) {
      entries.add(new AbstractMap.SimpleEntry<>(key, cache.get(key)));
    }
    return io.vavr.collection.HashSet.ofAll(entries).map(this::toTuple2);
  }

  private Tuple2<DatabaseKey, DatabaseValue> toTuple2(Entry<DatabaseKey, DatabaseValue> entry) {
    return Tuple.of(entry.getKey(), entry.getValue());
  }
}
