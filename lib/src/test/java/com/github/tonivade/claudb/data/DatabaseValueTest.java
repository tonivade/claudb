/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Instant;
import org.junit.Test;

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
}
