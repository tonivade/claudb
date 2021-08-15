/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.claudb.data.DatabaseValue.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.claudb.data.DatabaseValue.list;
import static com.github.tonivade.claudb.data.DatabaseValue.score;
import static com.github.tonivade.claudb.data.DatabaseValue.set;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.claudb.data.DatabaseValue.zset;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.Map;
import java.util.NavigableSet;

import org.junit.Test;

import com.github.tonivade.resp.protocol.SafeString;

public class DatabaseValueTest {

  @Test
  public void testNoExpirationValue() {
    Instant now = Instant.now();

    DatabaseValue nonExpiredValue = string("hola");

    assertThat(nonExpiredValue.isExpired(now), is(false));
    assertThat(nonExpiredValue.timeToLiveMillis(now), is(-1L));
    assertThat(nonExpiredValue.timeToLiveSeconds(now), is(-1));
  }

  @Test
  public void testExpiredKey() {
    Instant now = Instant.now();

    DatabaseValue expiredValue = string("hola").expiredAt(now.plusSeconds(10));

    assertThat(expiredValue.isExpired(now), is(false));
    assertThat(expiredValue.timeToLiveSeconds(now), is(10));
    assertThat(expiredValue.timeToLiveMillis(now), is(10000L));

    Instant expired = now.plusSeconds(11);

    assertThat(expiredValue.isExpired(expired), is(true));
    assertThat(expiredValue.timeToLiveMillis(expired), is(-1000L));
    assertThat(expiredValue.timeToLiveSeconds(expired), is(-1));
  }

  @Test
  public void getValue() {
    assertThat(string("hola").getString(), is(safeString("hola")));
  }

  @Test(expected = IllegalStateException.class)
  public void getValueMismatch() {
    string("hola").getList();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSortedSetUnmodifiable() {
    DatabaseValue value = zset(score(1.0, safeString("a")), score(2.0, safeString("b")), score(3.0, safeString("c")));

    NavigableSet<Map.Entry<Double, SafeString>> sortedSet = value.getSortedSet();

    sortedSet.add(score(1.0, safeString("d")));
  }

  @Test
  public void serializableTest() throws IOException, ClassNotFoundException {
    verifySerializable(list(safeString("hello world!")));
    verifySerializable(set(safeString("hello world!")));
    verifySerializable(hash(entry(safeString("key"), safeString("value"))));
    verifySerializable(zset(score(1., safeString("value"))));
    verifySerializable(string("hello world!"));
  }

  private void verifySerializable(DatabaseValue value) throws IOException, ClassNotFoundException {
    ByteArrayOutputStream array = new ByteArrayOutputStream();
    ObjectOutputStream output = new ObjectOutputStream(array);
    output.writeObject(value);
    output.flush();

    ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(array.toByteArray()));
    assertThat(value, equalTo(input.readObject()));
  }
}
