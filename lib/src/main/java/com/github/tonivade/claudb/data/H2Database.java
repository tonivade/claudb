package com.github.tonivade.claudb.data;

import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.ImmutableMap;
import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.resp.protocol.SafeString;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;

public class H2Database implements Database {

  private static final DatabaseBuilder BUILDER = new DatabaseBuilder();

  private final MVStore store;

  public H2Database() {
    this.store = new MVStore.Builder().autoCommitDisabled().open();
  }

  @Override
  public int size() {
    return getMap().size();
  }

  @Override
  public boolean containsKey(DatabaseKey key) {
    return getMap().containsKey(key);
  }

  @Override
  public DatabaseValue get(DatabaseKey key) {
    MVMap<DatabaseKey, DatabaseValue> data = getMap();
    DatabaseValue value = data.get(key);
    if (value != null) {
      if (!value.isExpired(Instant.now())) {
        return value;
      }
      data.remove(key);
      store.commit();
    }
    return null;
  }

  @Override
  public DatabaseValue put(DatabaseKey key, DatabaseValue value) {
    DatabaseValue result = getMap().put(key, value);
    store.commit();
    return result;
  }

  @Override
  public DatabaseValue remove(DatabaseKey key) {
    DatabaseValue result = getMap().remove(key);
    store.commit();
    return result;
  }

  @Override
  public void clear() {
    getMap().clear();
    store.commit();
  }

  @Override
  public ImmutableSet<DatabaseKey> keySet() {
    return ImmutableSet.from(getMap().keySet());
  }

  @Override
  public Sequence<DatabaseValue> values() {
    return ImmutableList.from(getMap().values());
  }

  @Override
  public ImmutableSet<Tuple2<DatabaseKey, DatabaseValue>> entrySet() {
    return ImmutableSet.from(getMap().entrySet()).map(Tuple::from);
  }

  private MVMap<DatabaseKey, DatabaseValue> getMap() {
    return store.openMap("data", BUILDER);
  }

  private static final class DatabaseBuilder extends MVMap.Builder<DatabaseKey, DatabaseValue> {

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
          ImmutableMap<SafeString, SafeString> hash = value.getHash();
          int hashSize = typeSize() + lengthSize();
          for (Tuple2<SafeString, SafeString> entry : hash.entries()) {
            hashSize += stringSize(entry.get1());
            hashSize += stringSize(entry.get2());
          }
          return hashSize + ttlSize(value.getExpiredAt());
        case LIST:
          ImmutableList<SafeString> list = value.getList();
          int listSize = typeSize() + lengthSize();
          for (SafeString safeString : list) {
            listSize += stringSize(safeString);
          }
          return listSize + ttlSize(value.getExpiredAt());
        case SET:
          ImmutableSet<SafeString> set = value.getSet();
          int setSize = typeSize() + lengthSize();
          for (SafeString safeString : set) {
            setSize += stringSize(safeString);
          }
          return setSize + ttlSize(value.getExpiredAt());
        case ZSET:
          NavigableSet<Map.Entry<Double, SafeString>> sortedSet = value.getSortedSet();
          int sortedSetSize = typeSize() + lengthSize();
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
          ImmutableMap<SafeString, SafeString> hash = value.getHash();
          writeLength(buff, hash.size());
          for (Tuple2<SafeString, SafeString> entry : hash.entries()) {
            writeString(buff, entry.get1());
            writeString(buff, entry.get2());
          }
          break;
        case LIST:
          ImmutableList<SafeString> list = value.getList();
          writeLength(buff, list.size());
          for (SafeString safeString : list) {
            writeString(buff, safeString);
          }
          break;
        case SET:
          ImmutableSet<SafeString> set = value.getSet();
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
          return withExpireAt(buff, DatabaseValue.string(safeString));
        case HASH:
          int length = readLength(buff);
          List<Tuple2<SafeString, SafeString>> entries = new ArrayList<>();
          for (int i = 0; i < length; i++) {
            entries.add(Tuple.of(readString(buff), readString(buff)));
          }
          return withExpireAt(buff, DatabaseValue.hash(entries));
        case LIST:
          int listLength = readLength(buff);
          List<SafeString> list = new ArrayList<>();
          for (int i = 0; i < listLength; i++) {
            list.add(readString(buff));
          }
          return withExpireAt(buff, DatabaseValue.list(list));
        case SET:
          int setLength = readLength(buff);
          List<SafeString> set = new ArrayList<>();
          for (int i = 0; i < setLength; i++) {
            set.add(readString(buff));
          }
          return withExpireAt(buff, DatabaseValue.set(set));
        case ZSET:
          int sortedSetLength = readLength(buff);
          List<Map.Entry<Double, SafeString>> sortedSet = new ArrayList<>();
          for (int i = 0; i < sortedSetLength; i++) {
            sortedSet.add(new AbstractMap.SimpleEntry<>(readScore(buff), readString(buff)));
          }
          return withExpireAt(buff, DatabaseValue.zset(sortedSet));
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
    buf.putInt(safeString.length());
    buf.put(safeString.getBytes());
  }

  private static void writeType(WriteBuffer buf, com.github.tonivade.claudb.data.DataType type) {
    buf.put((byte) type.ordinal());
  }

  private static void writeLength(WriteBuffer buf, int length) {
    buf.putInt(length);
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
    return buf.getInt();
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
    return lengthSize() + string.length();
  }

  private static int lengthSize() {
    return Integer.BYTES;
  }

  private static int ttlSize(Instant instant) {
    return Byte.BYTES + (instant != null ? Long.BYTES : 0);
  }
}
