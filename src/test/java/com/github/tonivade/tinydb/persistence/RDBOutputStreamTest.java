/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.persistence;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.entry;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.list;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.score;
import static com.github.tonivade.tinydb.DatabaseValueMatchers.set;
import static com.github.tonivade.tinydb.data.DatabaseValue.hash;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;
import static com.github.tonivade.tinydb.data.DatabaseValue.zset;
import static com.github.tonivade.tinydb.util.HexUtil.toHexString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.OnHeapDatabaseFactory;

public class RDBOutputStreamTest {

  private ByteBufferOutputStream baos;
  private RDBOutputStream out;

  @Before
  public void setUp()  {
    baos = new ByteBufferOutputStream();
    out = new RDBOutputStream(baos);
  }

  @Test
  public void testStartEnd() throws IOException {
    out.preamble(3);
    out.select(0);
    out.end();

    assertThat(toHexString(baos.toByteArray()), is("524544495330303033FE00FF77DE0394AC9D23EA"));
  }

  @Test
  public void testString() throws IOException  {
    out.dabatase(database().add(safeKey("a"), string("test")).build());

    assertThat(toHexString(baos.toByteArray()), is("0001610474657374"));
  }

  @Test
  public void testStringTtl() throws IOException  {
    out.dabatase(database().add(new DatabaseKey(safeString("a")), string("test").expiredAt(Instant.ofEpochMilli(1L))).build());

    assertThat(toHexString(baos.toByteArray()), is("FC00000000000000010001610474657374"));
  }

  @Test
  public void testList() throws IOException  {
    out.dabatase(database().add(safeKey("a"), list("test")).build());

    assertThat(toHexString(baos.toByteArray()), is("010161010474657374"));
  }

  @Test
  public void testSet() throws IOException  {
    out.dabatase(database().add(safeKey("a"), set("test")).build());

    assertThat(toHexString(baos.toByteArray()), is("020161010474657374"));
  }

  @Test
  public void testSortedSet() throws IOException  {
    out.dabatase(database().add(safeKey("a"), zset(score(1.0, "test"))).build());

    assertThat(toHexString(baos.toByteArray()), is("03016101047465737403312E30"));
  }

  @Test
  public void testHash() throws IOException  {
    out.dabatase(database().add(safeKey("a"), hash(entry("1", "test"))).build());

    assertThat(toHexString(baos.toByteArray()), is("0401610101310474657374"));
  }

  @Test
  public void testAll() throws IOException  {
    out.preamble(3);
    out.select(0);
    out.dabatase(database().add(safeKey("a"), string("test")).build());
    out.select(1);
    out.dabatase(database().add(safeKey("a"), list("test")).build());
    out.select(2);
    out.dabatase(database().add(safeKey("a"), set("test")).build());
    out.select(3);
    out.dabatase(database().add(safeKey("a"), zset(score(1.0, "test"))).build());
    out.select(4);
    out.dabatase(database().add(safeKey("a"), hash(entry("1", "test"))).build());
    out.select(5);
    out.dabatase(database().add(new DatabaseKey(safeString("a")), string("test").expiredAt(Instant.ofEpochMilli(1L))).build());
    out.end();

    assertThat(toHexString(baos.toByteArray()), is("524544495330303033FE000001610474657374FE01010161010474657374FE02020161010474657374FE0303016101047465737403312E30FE040401610101310474657374FE05FC00000000000000010001610474657374FFA9D1F09C463A7043"));
  }

  public static DatabaseBuiler database() {
    return new DatabaseBuiler();
  }

  private static class DatabaseBuiler {

    private final Database db = new OnHeapDatabaseFactory().create("test");

    public DatabaseBuiler add(DatabaseKey key, DatabaseValue value) {
      db.put(key, value);
      return this;
    }

    public Database build() {
      return db;
    }
  }

}
