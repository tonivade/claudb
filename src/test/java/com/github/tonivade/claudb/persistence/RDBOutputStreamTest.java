/*
 * Copyright (c) 2015-2019, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.persistence;

import static com.github.tonivade.claudb.DatabaseValueMatchers.entry;
import static com.github.tonivade.claudb.DatabaseValueMatchers.list;
import static com.github.tonivade.claudb.DatabaseValueMatchers.score;
import static com.github.tonivade.claudb.DatabaseValueMatchers.set;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.claudb.data.OnHeapDatabaseFactory;
import com.github.tonivade.resp.protocol.SafeString;

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

    assertThat(toHexString(baos.toByteArray()), is("524544495330303033fe00ff77de0394ac9d23ea"));
  }

  @Test
  public void testString() throws IOException  {
    out.dabatase(database().add(safeKey("a"), string("test")).build());

    assertThat(toHexString(baos.toByteArray()), is("0001610474657374"));
  }

  @Test
  public void testStringTtl() throws IOException  {
    out.dabatase(database().add(new DatabaseKey(safeString("a")), string("test").expiredAt(Instant.ofEpochMilli(1L))).build());

    assertThat(toHexString(baos.toByteArray()), is("fc00000000000000010001610474657374"));
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

    assertThat(toHexString(baos.toByteArray()), is("03016101047465737403312e30"));
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

    assertThat(toHexString(baos.toByteArray()), is("524544495330303033fe000001610474657374fe01010161010474657374fe02020161010474657374fe0303016101047465737403312e30fe040401610101310474657374fe05fc00000000000000010001610474657374ffa9d1f09c463a7043"));
  }

  private String toHexString(byte[] byteArray) {
    return new SafeString(byteArray).toHexString();
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
