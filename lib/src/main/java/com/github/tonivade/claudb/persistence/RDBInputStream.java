/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.persistence;

import static com.github.tonivade.claudb.data.DatabaseValue.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.claudb.data.DatabaseValue.list;
import static com.github.tonivade.claudb.data.DatabaseValue.score;
import static com.github.tonivade.claudb.data.DatabaseValue.set;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;
import static com.github.tonivade.claudb.persistence.ByteUtils.byteArrayToInt;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.time.Instant.ofEpochMilli;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.CheckedInputStream;

import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.resp.protocol.SafeString;

public class RDBInputStream {

  private static final SafeString REDIS_PREAMBLE = safeString("REDIS");

  private static final long TO_MILLIS = 1000L;

  private static final int HASH = 0x04;
  private static final int SORTED_SET = 0x03;
  private static final int SET = 0x02;
  private static final int LIST = 0x01;
  private static final int STRING = 0x00;

  private static final int TTL_MILLISECONDS = 0xFC;
  private static final int TTL_SECONDS = 0xFD;
  private static final int SELECT = 0xFE;
  private static final int END_OF_STREAM = 0xFF;

  private static final int REDIS_VERSION = 6;
  private static final int VERSION_LENGTH = 4;
  private static final int REDIS_LENGTH = 5;

  private final CheckedInputStream in;

  public RDBInputStream(InputStream in) {
    this.in = new CheckedInputStream(requireNonNull(in), new CRC64());
  }

  public Map<Integer, Map<DatabaseKey, DatabaseValue>> parse() throws IOException {
    Map<Integer, Map<DatabaseKey, DatabaseValue>> databases = new HashMap<>();

    int version = version();

    if (version > REDIS_VERSION) {
      throw new IOException("invalid version: " + version);
    }

    Long expireTime = null;
    HashMap<DatabaseKey, DatabaseValue> db = null;
    for (boolean end = false; !end;) {
      int read = in.read();
      switch (read) {
      case SELECT:
        db = new HashMap<>();
        databases.put(readLength(), db);
        break;
      case TTL_SECONDS:
        expireTime = parseTimeSeconds();
        break;
      case TTL_MILLISECONDS:
        expireTime = parseTimeMillis();
        break;
      case STRING:
        ensure(db, readKey(), readString(expireTime));
        expireTime = null;
        break;
      case LIST:
        ensure(db, readKey(), readList(expireTime));
        expireTime = null;
        break;
      case SET:
        ensure(db, readKey(), readSet(expireTime));
        expireTime = null;
        break;
      case SORTED_SET:
        ensure(db, readKey(), readSortedSet(expireTime));
        expireTime = null;
        break;
      case HASH:
        ensure(db, readKey(), readHash(expireTime));
        expireTime = null;
        break;
      case END_OF_STREAM:
        // end of stream
        end = true;
        db = null;
        expireTime = null;
        break;
      default:
        throw new IOException("not supported: " + read);
      }
    }

    verifyChecksum();

    return databases;
  }

  private long parseTimeSeconds() throws IOException {
    byte[] seconds = read(Integer.BYTES);
    return ByteUtils.byteArrayToInt(seconds) * TO_MILLIS;
  }

  private long parseTimeMillis() throws IOException {
    byte[] millis = read(Long.BYTES);
    return ByteUtils.byteArrayToLong(millis);
  }

  private void verifyChecksum() throws IOException {
    long calculated = in.getChecksum().getValue();

    long readed = parseChecksum();

    if (calculated != readed) {
      throw new IOException("invalid checksum: " + readed);
    }
  }

  private long parseChecksum() throws IOException {
    return ByteUtils.byteArrayToLong(read(Long.BYTES));
  }

  private int version() throws IOException {
    SafeString redis = new SafeString(read(REDIS_LENGTH));
    if (!redis.equals(REDIS_PREAMBLE)) {
      throw new IOException("not valid stream");
    }
    return parseVersion(read(VERSION_LENGTH));
  }

  private int parseVersion(byte[] version) {
    StringBuilder sb = new StringBuilder();
    for (byte b : version) {
      sb.append((char) b);
    }
    return Integer.parseInt(sb.toString());
  }

  private DatabaseValue readString(Long expireTime) throws IOException {
    return string(readSafeString()).expiredAt(expireTime != null ? ofEpochMilli(expireTime) : null);
  }

  private DatabaseValue readList(Long expireTime) throws IOException {
    int size = readLength();
    List<SafeString> list = new LinkedList<>();
    for (int i = 0; i < size; i++) {
      list.add(readSafeString());
    }
    return list(list).expiredAt(expireTime != null ? ofEpochMilli(expireTime) : null);
  }

  private DatabaseValue readSet(Long expireTime) throws IOException {
    int size = readLength();
    Set<SafeString> set = new LinkedHashSet<>();
    for (int i = 0; i < size; i++) {
      set.add(readSafeString());
    }
    return set(set).expiredAt(expireTime != null ? ofEpochMilli(expireTime) : null);
  }

  private DatabaseValue readSortedSet(Long expireTime) throws IOException {
    int size = readLength();
    Set<Entry<Double, SafeString>> entries = new LinkedHashSet<>();
    for (int i = 0; i < size; i++) {
      SafeString value = readSafeString();
      Double score = readDouble();
      entries.add(score(score, value));
    }
    return zset(entries).expiredAt(expireTime != null ? ofEpochMilli(expireTime) : null);
  }

  private DatabaseValue readHash(Long expireTime) throws IOException {
    int size = readLength();
    Set<Tuple2<SafeString, SafeString>> entries = new LinkedHashSet<>();
    for (int i = 0; i < size; i++) {
      entries.add(entry(readSafeString(), readSafeString()));
    }
    return hash(entries).expiredAt(expireTime != null ? ofEpochMilli(expireTime) : null);
  }

  private void ensure(Map<DatabaseKey, DatabaseValue> db, DatabaseKey key, DatabaseValue value) throws IOException {
    if (db != null) {
      if (!value.isExpired(Instant.now())) {
        db.put(key, value);
      }
    } else {
      throw new IOException("no database selected");
    }
  }

  private int readLength() throws IOException {
    int length = in.read();
    if (length < 0x40) {
      // 1 byte: 00XXXXXX
      return length;
    } else if (length < 0x80) {
      // 2 bytes: 01XXXXXX XXXXXXXX
      int next = in.read();
      return readLength(length, next);
    } else {
      // 5 bytes: 10...... XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
      return byteArrayToInt(read(Integer.BYTES));
    }
  }

  private int readLength(int length, int next) {
    return ((length & 0x3F) << 8) | (next & 0xFF);
  }

  private SafeString readSafeString() throws IOException {
    int length = readLength();
    return new SafeString(read(length));
  }

  private DatabaseKey readKey() throws IOException {
    return new DatabaseKey(readSafeString());
  }

  private Double readDouble() throws IOException {
    return Double.parseDouble(readSafeString().toString());
  }

  private byte[] read(int size) throws IOException {
    byte[] array = new byte[size];
    int read = in.read(array);
    if (read != size) {
      throw new IOException("error reading stream");
    }
    return array;
  }
}
