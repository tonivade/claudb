/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.data;

import static java.time.Instant.now;
import static tonivade.equalizer.Equalizer.equalizer;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.github.tonivade.resp.protocol.SafeString;

public class DatabaseKey implements Comparable<DatabaseKey> {

  private final Instant expiredAt;
  private final SafeString value;

  public DatabaseKey(SafeString value, Instant expiredAt) {
    super();
    this.value = value;
    this.expiredAt = expiredAt;
  }

  public SafeString getValue() {
    return value;
  }

  public boolean isExpired(Instant now) {
    if (expiredAt != null) {
      return now.isAfter(expiredAt);
    }
    return false;
  }

  public long timeToLiveMillis(Instant now) {
    if (expiredAt != null) {
      return timeToLive(now);
    }
    return -1;
  }

  public int timeToLiveSeconds(Instant now) {
    if (expiredAt != null) {
      return (int) Math.floorDiv(timeToLive(now), 1000L);
    }
    return -1;
  }

  public Instant expiredAt() {
    return expiredAt;
  }

  @Override
  public int compareTo(DatabaseKey o) {
    return value.compareTo(o.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public boolean equals(Object obj) {
    return equalizer(this)
        .append((one, other) -> Objects.equals(one.value, other.value))
        .applyTo(obj);
  }

  @Override
  public String toString() {
    return value.toString();
  }

  public static DatabaseKey safeKey(SafeString str) {
    return new DatabaseKey(str, null);
  }

  public static DatabaseKey safeKey(SafeString str, int ttlSeconds) {
    return safeKey(str, TimeUnit.SECONDS.toMillis(ttlSeconds));
  }

  public static DatabaseKey safeKey(SafeString str, long ttlMillis) {
    return new DatabaseKey(str, now().plusMillis(ttlMillis));
  }

  private long timeToLive(Instant now) {
    return Duration.between(now, expiredAt).toMillis();
  }
}
