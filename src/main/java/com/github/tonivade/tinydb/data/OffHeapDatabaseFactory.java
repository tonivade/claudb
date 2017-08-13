/*
 * Copyright (c) 2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.data;

import java.nio.ByteBuffer;

import org.caffinitas.ohc.CacheSerializer;
import org.caffinitas.ohc.OHCache;
import org.caffinitas.ohc.OHCacheBuilder;
import org.nustaq.serialization.FSTConfiguration;

public class OffHeapDatabaseFactory implements DatabaseFactory {

  @Override
  public Database create(String name) {
    OHCacheBuilder<DatabaseKey, DatabaseValue> builder = OHCacheBuilder.<DatabaseKey, DatabaseValue>newBuilder();
    OHCache<DatabaseKey, DatabaseValue> cache = builder.keySerializer(new FSTSerializer<>()).valueSerializer(new FSTSerializer<>()).build();
    return new OffHeapDatabase(cache);
  }

  @Override
  public void clear() {

  }
  
  private static class FSTSerializer<E> implements CacheSerializer<E> {
    
    FSTConfiguration fst = FSTConfiguration.createDefaultConfiguration();

    @Override
    public void serialize(E value, ByteBuffer buf) {
      byte[] array = fst.asByteArray(value);
      buf.putInt(array.length);
      buf.put(array);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E deserialize(ByteBuffer buf) {
      int length = buf.getInt();
      byte[] array = new byte[length];
      buf.get(array);
      return (E) fst.asObject(array);
    }

    @Override
    public int serializedSize(E value) {
      return fst.asByteArray(value).length + Integer.BYTES;
    }
  }
}
