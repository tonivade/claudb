/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.data;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseValue.score;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;
import static com.github.tonivade.tinydb.data.DatabaseValue.zset;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
  public void testExpiredKey() throws InterruptedException {
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

  @Test(expected = UnsupportedOperationException.class)
  public void testSortedSetUnmodifiable() {
    DatabaseValue value = zset(score(1.0, safeString("a")), score(2.0, safeString("b")), score(3.0, safeString("c")));

    NavigableSet<Map.Entry<Double, SafeString>> sortedSet = value.getValue();

    sortedSet.add(score(1.0, safeString("d")));
  }
}
