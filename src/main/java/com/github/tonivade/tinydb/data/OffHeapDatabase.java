/*
 * Copyright (c) 2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.data;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caffinitas.ohc.CloseableIterator;
import org.caffinitas.ohc.OHCache;

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
  public boolean containsKey(Object key) {
    return cache.containsKey((DatabaseKey) key);
  }

  @Override
  public boolean containsValue(Object value) {
    throw new RuntimeException();
  }

  @Override
  public DatabaseValue get(Object key) {
    return cache.get((DatabaseKey) key);
  }

  @Override
  public DatabaseValue put(DatabaseKey key, DatabaseValue value) {
    cache.put(key, value);
    return value;
  }

  @Override
  public DatabaseValue remove(Object key) {
    DatabaseValue value = cache.get((DatabaseKey) key);
    cache.remove((DatabaseKey) key);
    return value;
  }

  @Override
  public void putAll(Map<? extends DatabaseKey, ? extends DatabaseValue> m) {
    for (Entry<? extends DatabaseKey, ? extends DatabaseValue> entry : m.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public Set<DatabaseKey> keySet() {
    Set<DatabaseKey> keys = new HashSet<>();
    try (CloseableIterator<DatabaseKey> iterator = cache.keyIterator()) {
      while(iterator.hasNext()) {
        keys.add(iterator.next());
      }
    } catch(IOException e) {
      // FIXME
    }
    return keys;
  }

  @Override
  public Collection<DatabaseValue> values() {
    List<DatabaseValue> values = new LinkedList<>();
    for (DatabaseKey key : keySet()) {
      values.add(cache.get(key));
    }
    return values;
  }

  @Override
  public Set<Map.Entry<DatabaseKey, DatabaseValue>> entrySet() {
    Set<Map.Entry<DatabaseKey, DatabaseValue>> entries = new HashSet<>();
    for (DatabaseKey key : keySet()) {
      entries.add(new AbstractMap.SimpleEntry<>(key, cache.get(key)));
    }
    return entries;
  }

  @Override
  public boolean isType(DatabaseKey key, DataType type) {
    DatabaseValue value = cache.get(key);
    return value != null ? value.getType() == type : true;
  }

  @Override
  public boolean rename(DatabaseKey from, DatabaseKey to) {
    DatabaseValue value = remove(from);
    if (value != null) {
      cache.put(to, value);
      return true;
    }
    return false;
  }
}
