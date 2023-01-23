/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.persistence;

import static com.github.tonivade.claudb.persistence.ByteUtils.lengthToByteArray;
import static com.github.tonivade.claudb.persistence.ByteUtils.toByteArray;
import static com.github.tonivade.resp.util.Precondition.checkNonNull;
import static com.github.tonivade.resp.protocol.SafeString.safeString;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.zip.CheckedOutputStream;

import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.resp.protocol.SafeString;

public class RDBOutputStream {

  private static final byte[] REDIS = safeString("REDIS").getBytes();
  private static final int RDB_VERSION = 6;

  private static final int TTL_MILLISECONDS = 0xFC;
  private static final int END_OF_STREAM = 0xFF;
  private static final int SELECT = 0xFE;

  private final CheckedOutputStream out;

  public RDBOutputStream(OutputStream out) {
    this.out = new CheckedOutputStream(checkNonNull(out), new CRC64());
  }

  public void write(List<Database> databases) throws IOException {
    preamble(RDB_VERSION);
    for (int i = 0; i < databases.size(); i++) {
      Database db = databases.get(i);
      if (!db.isEmpty()) {
        select(i);
        dabatase(db);
      }
    }
    end();
  }

  void preamble(int version) throws IOException {
    out.write(REDIS);
    out.write(version(version));
  }

  byte[] version(int version) {
    StringBuilder sb = new StringBuilder(String.valueOf(version));
    for (int i = sb.length(); i < Integer.BYTES; i++) {
      sb.insert(0, '0');
    }
    return sb.toString().getBytes(StandardCharsets.UTF_8);
  }

  void select(int db) throws IOException {
    out.write(SELECT);
    length(db);
  }

  void dabatase(Database db) throws IOException {
    for (Map.Entry<DatabaseKey, DatabaseValue> entry : db.entrySet()) {
      value(entry.getKey(), entry.getValue());
    }
  }

  private void value(DatabaseKey key, DatabaseValue value) throws IOException {
    expiredAt(value.getExpiredAt());
    type(value.getType());
    key(key);
    value(value);
  }

  private void expiredAt(Instant expiredAt) throws IOException {
    if (expiredAt != null) {
      out.write(TTL_MILLISECONDS);
      out.write(ByteUtils.toByteArray(expiredAt.toEpochMilli()));
    }
  }

  private void type(DataType type) throws IOException {
    out.write(type.ordinal());
  }

  private void key(DatabaseKey key) throws IOException {
    string(key.getValue());
  }

  private void value(DatabaseValue value) throws IOException {
    switch (value.getType()) {
    case STRING:
      string(value.getString());
      break;
    case LIST:
      list(value.getList());
      break;
    case HASH:
      hash(value.getHash());
      break;
    case SET:
      set(value.getSet());
      break;
    case ZSET:
      zset(value.getSortedSet());
      break;
    default:
      break;
    }
  }

  private void length(int length) throws IOException {
    out.write(lengthToByteArray(length));
  }

  private void string(String value) throws IOException {
    string(safeString(value));
  }

  private void string(SafeString value) throws IOException {
    byte[] bytes = value.getBytes();
    length(bytes.length);
    out.write(bytes);
  }

  private void string(double value) throws IOException {
    string(String.valueOf(value));
  }

  private void list(List<SafeString> value) throws IOException {
    length(value.size());
    for (SafeString item : value) {
      string(item);
    }
  }

  private void hash(Map<SafeString, SafeString> value) throws IOException {
    length(value.size());
    for (Map.Entry<SafeString, SafeString> entry : value.entrySet()) {
      string(entry.getKey());
      string(entry.getValue());
    }
  }

  private void set(Set<SafeString> value) throws IOException {
    length(value.size());
    for (SafeString item : value) {
      string(item);
    }
  }

  private void zset(NavigableSet<Entry<Double, SafeString>> value) throws IOException {
    length(value.size());
    for (Entry<Double, SafeString> item : value) {
      string(item.getValue());
      string(item.getKey());
    }
  }

  void end() throws IOException {
    out.write(END_OF_STREAM);
    out.write(toByteArray(out.getChecksum().getValue()));
    out.flush();
  }
}
