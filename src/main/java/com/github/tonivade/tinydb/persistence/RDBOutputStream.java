/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.persistence;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.persistence.ByteUtils.toByteArray;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.zip.CheckedOutputStream;

import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

public class RDBOutputStream {

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  private static final byte[] REDIS = safeString("REDIS").getBytes();

  private static final int TTL_MILISECONDS = 0xFC;
  private static final int END_OF_STREAM = 0xFF;
  private static final int SELECT = 0xFE;

  private final CheckedOutputStream out;

  public RDBOutputStream(OutputStream out) {
    super();
    this.out = new CheckedOutputStream(out, new CRC64());
  }

  public void preamble(int version) throws IOException {
    out.write(REDIS);
    out.write(version(version));
  }

  private byte[] version(int version) throws IOException {
    StringBuilder sb = new StringBuilder(String.valueOf(version));
    for (int i = sb.length(); i < Integer.BYTES; i++) {
      sb.insert(0, '0');
    }
    return sb.toString().getBytes(DEFAULT_CHARSET);
  }

  public void select(int db) throws IOException {
    out.write(SELECT);
    length(db);
  }

  public void dabatase(Database db) throws IOException {
    for (Tuple2<DatabaseKey, DatabaseValue> entry : db.entrySet()) {
      value(entry._1(), entry._2());
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
      out.write(TTL_MILISECONDS);
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
    if (length < 0x40) {
      // 1 byte: 00XXXXXX
      out.write(length);
    } else if (length < 0x4000) {
      // 2 bytes: 01XXXXXX XXXXXXXX
      out.write(0x4000 & length);
    } else {
      // 5 bytes: 10...... XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
      out.write(0x80);
      out.write(toByteArray(length));
    }
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
    for (Tuple2<SafeString, SafeString> entry : value) {
      string(entry._1());
      string(entry._2());
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

  public void end() throws IOException {
    out.write(END_OF_STREAM);
    out.write(toByteArray(out.getChecksum().getValue()));
    out.flush();
  }
}
