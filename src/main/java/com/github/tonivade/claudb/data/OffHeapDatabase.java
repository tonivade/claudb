/*
 * Copyright (c) 2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.purefun.data.Sequence;
import org.caffinitas.ohc.CloseableIterator;
import org.caffinitas.ohc.OHCache;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class OffHeapDatabase implements Database {

  private final OHCache<DatabaseKey, DatabaseValue> cache;

  public OffHeapDatabase(OHCache<DatabaseKey, DatabaseValue> cache) {
    this.cache = requireNonNull(cache);
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
    DatabaseValue value = get(key);
    cache.remove(key);
    return value;
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public ImmutableSet<DatabaseKey> keySet() {
    Set<DatabaseKey> keys = new HashSet<>();
    try (CloseableIterator<DatabaseKey> iterator = cache.keyIterator()) {
      while (iterator.hasNext()) {
        keys.add(iterator.next());
      }
    } catch(IOException e) {
      throw new UncheckedIOException(e);
    }
    return ImmutableSet.from(keys);
  }

  @Override
  public Sequence<DatabaseValue> values() {
    List<DatabaseValue> values = new LinkedList<>();
    for (DatabaseKey key : keySet()) {
      values.add(cache.get(key));
    }
    return ImmutableList.from(values);
  }

  @Override
  public ImmutableSet<Tuple2<DatabaseKey, DatabaseValue>> entrySet() {
    return keySet().map(key -> Tuple.of(key, get(key)));
  }
}
