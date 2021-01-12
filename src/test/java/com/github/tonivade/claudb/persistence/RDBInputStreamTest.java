/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.persistence;

import static com.github.tonivade.resp.protocol.SafeString.fromHexString;
import static com.github.tonivade.claudb.DatabaseValueMatchers.entry;
import static com.github.tonivade.claudb.DatabaseValueMatchers.list;
import static com.github.tonivade.claudb.DatabaseValueMatchers.score;
import static com.github.tonivade.claudb.DatabaseValueMatchers.set;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Test;

import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;

public class RDBInputStreamTest {

  @Test
  public void testEmpty() throws IOException {
    RDBInputStream in = new RDBInputStream(array("524544495330303033FE00FF77DE0394AC9D23EA"));

    Map<Integer, Map<DatabaseKey, DatabaseValue>> databases = in.parse();

    assertThat(databases.size(), is(1));
  }

  @Test
  public void testBig() throws IOException {
    RDBInputStream in = new RDBInputStream(array("524544495330303033FE00000161406474657374746573747465737474657374746573747465737474657374746573747465737474657374746573747465737474657374746573747465737474657374746573747465737474657374746573747465737474657374746573747465737474657374FFC56EDB43146A8431"));

    Map<Integer, Map<DatabaseKey, DatabaseValue>> databases = in.parse();

    assertThat(databases.size(), is(1));

    assertDB(databases.get(0), string("testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"));
  }

  @Test
  public void testAll() throws IOException {
    RDBInputStream in = new RDBInputStream(array("524544495330303033FE000001610474657374FE01010161010474657374FE02020161010474657374FE0303016101047465737403312E30FE040401610101310474657374FE05FC00000000000000010001610474657374FFA9D1F09C463A7043"));

    Map<Integer, Map<DatabaseKey, DatabaseValue>> databases = in.parse();

    assertThat(databases.size(), is(6));

    assertDB(databases.get(0), string("test"));
    assertDB(databases.get(1), list("test"));
    assertDB(databases.get(2), set("test"));
    assertDB(databases.get(3), zset(score(1.0, "test")));
    assertDB(databases.get(4), hash(entry("1", "test")));
    assertThat(databases.get(5), notNullValue());
    assertThat(databases.get(5).isEmpty(), is(true));
  }

  private void assertDB(Map<DatabaseKey, DatabaseValue> db, DatabaseValue value) {
    assertThat(db, notNullValue());
    assertThat(db.get(safeKey("a")), is(value));
  }

  private InputStream array(String string) {
    return new ByteBufferInputStream(fromHexString(string).getBytes());
  }
}
