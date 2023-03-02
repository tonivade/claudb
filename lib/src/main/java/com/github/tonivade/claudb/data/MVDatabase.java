/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.claudb.data.DatabaseValue.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.claudb.data.DatabaseValue.list;
import static com.github.tonivade.claudb.data.DatabaseValue.set;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;
import static com.github.tonivade.resp.util.Precondition.checkNonNull;
import com.github.tonivade.claudb.persistence.ByteUtils;
import com.github.tonivade.resp.protocol.SafeString;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;

public class MVDatabase implements Database {

  private final MVMap<DatabaseKey, DatabaseValue> map;

  public MVDatabase(MVMap<DatabaseKey, DatabaseValue> map) {
    this.map = checkNonNull(map);
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean containsKey(DatabaseKey key) {
    return map.containsKey(key);
  }

  @Override
  public DatabaseValue get(DatabaseKey key) {
    DatabaseValue value = map.get(key);
    if (value != null) {
      if (!value.isExpired(Instant.now())) {
        return value;
      }
      map.remove(key);
    }
    return null;
  }

  @Override
  public DatabaseValue put(DatabaseKey key, DatabaseValue value) {
    return map.put(key, value);
  }

  @Override
  public DatabaseValue remove(DatabaseKey key) {
    return map.remove(key);
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public Set<DatabaseKey> keySet() {
    return map.keySet();
  }

  @Override
  public Collection<DatabaseValue> values() {
    return map.values();
  }

  @Override
  public Set<Map.Entry<DatabaseKey, DatabaseValue>> entrySet() {
    return map.entrySet();
  }

  static final class DatabaseBuilder extends MVMap.Builder<DatabaseKey, DatabaseValue> {

    public DatabaseBuilder() {
      setKeyType(new DatabaseKeyType());
      setValueType(new DatabaseValueType());
    }
  }

  private static final class DatabaseKeyType extends BasicDataType<DatabaseKey> {

    @Override
    public int compare(DatabaseKey a, DatabaseKey b) {
      return a.compareTo(b);
    }

    @Override
    public DatabaseKey[] createStorage(int size) {
      return new DatabaseKey[size];
    }

    @Override
    public int getMemory(DatabaseKey key) {
      return stringSize(key.getValue());
    }

    @Override
    public void write(WriteBuffer buff, DatabaseKey key) {
      writeString(buff, key.getValue());
    }

    @Override
    public DatabaseKey read(ByteBuffer buff) {
      SafeString value = readString(buff);
      return new DatabaseKey(value);
    }
  }

  private static final class DatabaseValueType extends BasicDataType<DatabaseValue> {

    @Override
    public DatabaseValue[] createStorage(int size) {
      return new DatabaseValue[size];
    }

    @Override
    public int getMemory(DatabaseValue value) {
      switch (value.getType()) {
        case STRING:
          SafeString string = value.getString();
          return typeSize() + stringSize(string) + ttlSize(value.getExpiredAt());
        case HASH:
          Map<SafeString, SafeString> hash = value.getHash();
          int hashSize = typeSize() + lengthSize(hash.size());
          for (Map.Entry<SafeString, SafeString> entry : hash.entrySet()) {
            hashSize += stringSize(entry.getKey());
            hashSize += stringSize(entry.getValue());
          }
          return hashSize + ttlSize(value.getExpiredAt());
        case LIST:
          List<SafeString> list = value.getList();
          int listSize = typeSize() + lengthSize(list.size());
          for (SafeString safeString : list) {
            listSize += stringSize(safeString);
          }
          return listSize + ttlSize(value.getExpiredAt());
        case SET:
          Set<SafeString> set = value.getSet();
          int setSize = typeSize() + lengthSize(set.size());
          for (SafeString safeString : set) {
            setSize += stringSize(safeString);
          }
          return setSize + ttlSize(value.getExpiredAt());
        case ZSET:
          NavigableSet<Map.Entry<Double, SafeString>> sortedSet = value.getSortedSet();
          int sortedSetSize = typeSize() + lengthSize(sortedSet.size());
          for (Map.Entry<Double, SafeString> entry : sortedSet) {
            sortedSetSize += scoreSize() + stringSize(entry.getValue());
          }
          return sortedSetSize + ttlSize(value.getExpiredAt());
        default:
          throw new IllegalStateException();
      }
    }

    @Override
    public void write(WriteBuffer buff, DatabaseValue value) {
      writeType(buff, value.getType());
      switch (value.getType()) {
        case STRING:
          writeString(buff, value.getString());
          break;
        case HASH:
          Map<SafeString, SafeString> hash = value.getHash();
          writeLength(buff, hash.size());
          for (Map.Entry<SafeString, SafeString> entry : hash.entrySet()) {
            writeString(buff, entry.getKey());
            writeString(buff, entry.getValue());
          }
          break;
        case LIST:
          List<SafeString> list = value.getList();
          writeLength(buff, list.size());
          for (SafeString safeString : list) {
            writeString(buff, safeString);
          }
          break;
        case SET:
          Set<SafeString> set = value.getSet();
          writeLength(buff, set.size());
          for (SafeString safeString : set) {
            writeString(buff, safeString);
          }
          break;
        case ZSET:
          NavigableSet<Map.Entry<Double, SafeString>> sortedSet = value.getSortedSet();
          writeLength(buff, sortedSet.size());
          for (Map.Entry<Double, SafeString> entry : sortedSet) {
            writeScore(buff, entry.getKey());
            writeString(buff, entry.getValue());
          }
          break;
        case NONE:
        default:
          throw new IllegalStateException();
      }
      writeExpireAt(buff, value);
    }

    @Override
    public DatabaseValue read(ByteBuffer buff) {
      com.github.tonivade.claudb.data.DataType type = readType(buff);
      switch (type) {
        case STRING:
          SafeString safeString = readString(buff);
          return withExpireAt(buff, string(safeString));
        case HASH:
          int length = readLength(buff);
          List<Map.Entry<SafeString, SafeString>> entries = new ArrayList<>(length);
          for (int i = 0; i < length; i++) {
            entries.add(entry(readString(buff), readString(buff)));
          }
          return withExpireAt(buff, hash(entries));
        case LIST:
          int listLength = readLength(buff);
          List<SafeString> list = new ArrayList<>(listLength);
          for (int i = 0; i < listLength; i++) {
            list.add(readString(buff));
          }
          return withExpireAt(buff, list(list));
        case SET:
          int setLength = readLength(buff);
          Set<SafeString> set = new HashSet<>(setLength);
          for (int i = 0; i < setLength; i++) {
            set.add(readString(buff));
          }
          return withExpireAt(buff, set(set));
        case ZSET:
          int sortedSetLength = readLength(buff);
          List<Map.Entry<Double, SafeString>> sortedSet = new ArrayList<>(sortedSetLength);
          for (int i = 0; i < sortedSetLength; i++) {
            sortedSet.add(new AbstractMap.SimpleEntry<>(readScore(buff), readString(buff)));
          }
          return withExpireAt(buff, zset(sortedSet));
        default:
          throw new IllegalStateException();
      }
    }

    private DatabaseValue withExpireAt(ByteBuffer buf, DatabaseValue value) {
      if (readHasExpireAt(buf)) {
        return value.expiredAt(readInstant(buf));
      }
      return value;
    }
  }

  private static void writeString(WriteBuffer buf, SafeString safeString) {
    writeLength(buf, safeString.length());
    buf.put(safeString.getBytes());
  }

  private static void writeType(WriteBuffer buf, com.github.tonivade.claudb.data.DataType type) {
    buf.put((byte) type.ordinal());
  }

  private static void writeLength(WriteBuffer buf, int length) {
    buf.put(ByteUtils.lengthToByteArray(length));
  }

  private static void writeScore(WriteBuffer buf, double score) {
    buf.putDouble(score);
  }

  private static void writeExpireAt(WriteBuffer buf, DatabaseValue value) {
    if (value.getExpiredAt() != null) {
      buf.put((byte) 1);
      buf.putLong(value.getExpiredAt().toEpochMilli());
    } else {
      buf.put((byte) 0);
    }
  }

  private static Instant readInstant(ByteBuffer buf) {
    return Instant.ofEpochMilli(buf.getLong());
  }

  private static com.github.tonivade.claudb.data.DataType readType(ByteBuffer buf) {
    return com.github.tonivade.claudb.data.DataType.values()[buf.get()];
  }

  private static int readLength(ByteBuffer buf) {
    return ByteUtils.byteArrayToLength(buf::get);
  }

  private static double readScore(ByteBuffer buf) {
    return buf.getDouble();
  }

  private static SafeString readString(ByteBuffer buf) {
    int length = readLength(buf);
    byte[] array = new byte[length];
    buf.get(array);
    return new SafeString(array);
  }

  private static boolean readHasExpireAt(ByteBuffer buf) {
    byte hasExpireAt = buf.get();
    return hasExpireAt != 0;
  }

  private static int scoreSize() {
    return Double.BYTES;
  }

  private static int typeSize() {
    return Byte.BYTES;
  }

  private static int stringSize(SafeString string) {
    return lengthSize(string.length()) + string.length();
  }

  private static int lengthSize(int length) {
    if (length < 0x40) {
      return 1;
    } else if (length < 0x4000) {
      return 2;
    }
    return 1 + Integer.BYTES;
  }

  private static int ttlSize(Instant instant) {
    return Byte.BYTES + (instant != null ? Long.BYTES : 0);
  }
}
