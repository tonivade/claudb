/*
 * Copyright (c) 2019, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import java.nio.ByteBuffer;

import org.caffinitas.ohc.CacheSerializer;
import org.caffinitas.ohc.Eviction;
import org.caffinitas.ohc.OHCache;
import org.caffinitas.ohc.OHCacheBuilder;
import org.nustaq.serialization.FSTConfiguration;

import com.github.tonivade.resp.protocol.SafeString;

public class OffHeapDatabaseFactory implements DatabaseFactory {

  @Override
  public Database create(String name) {
    return new OffHeapDatabase(createCache());
  }

  private OHCache<DatabaseKey, DatabaseValue> createCache() {
    return builder()
        .eviction(Eviction.NONE)
        .throwOOME(true)
        .keySerializer(new FSTSerializer<>())
        .valueSerializer(new FSTSerializer<>())
        .build();
  }

  private OHCacheBuilder<DatabaseKey, DatabaseValue> builder() {
    return OHCacheBuilder.newBuilder();
  }

  @Override
  public void clear() {
    // nothing to do
  }

  private static class FSTSerializer<E> implements CacheSerializer<E> {

    private static final FSTConfiguration FST = FSTConfiguration.createDefaultConfiguration();

    static {
      FST.registerClass(DatabaseValue.class);
      FST.registerClass(DatabaseKey.class);
      FST.registerClass(SafeString.class);
      FST.registerClass(SortedSet.class);
    }

    @Override
    public void serialize(E value, ByteBuffer buf) {
      byte[] array = FST.asByteArray(value);
      buf.putInt(array.length);
      buf.put(array);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E deserialize(ByteBuffer buf) {
      int length = buf.getInt();
      byte[] array = new byte[length];
      buf.get(array);
      return (E) FST.asObject(array);
    }

    @Override
    public int serializedSize(E value) {
      return FST.asByteArray(value).length + Integer.BYTES;
    }
  }
}
