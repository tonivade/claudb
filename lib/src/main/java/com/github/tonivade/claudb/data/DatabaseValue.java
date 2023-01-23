/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.data;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.resp.util.Precondition.checkNonNull;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.resp.util.Equal;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class DatabaseValue {

  public static final DatabaseValue EMPTY_STRING = string("");
  public static final DatabaseValue EMPTY_LIST = list();
  public static final DatabaseValue EMPTY_SET = set();
  public static final DatabaseValue EMPTY_ZSET = zset();
  public static final DatabaseValue EMPTY_HASH = hash();
  public static final DatabaseValue NULL = null;

  private static final Equal<DatabaseValue> EQUAL =
      Equal.<DatabaseValue>of().comparing(v -> v.type).comparing(v -> v.value);

  private final DataType type;
  private final Object value;
  private final Instant expiredAt;

  private DatabaseValue(DataType type, Object value) {
    this(type, value, null);
  }

  private DatabaseValue(DataType type, Object value, Instant expiredAt) {
    this.type = checkNonNull(type);
    this.value = checkNonNull(value);
    this.expiredAt = expiredAt;
  }

  public DataType getType() {
    return type;
  }

  public SafeString getString() {
    requiredType(DataType.STRING);
    return getValue();
  }

  public List<SafeString> getList() {
    requiredType(DataType.LIST);
    return getValue();
  }

  public Set<SafeString> getSet() {
    requiredType(DataType.SET);
    return getValue();
  }

  public NavigableSet<Entry<Double, SafeString>> getSortedSet() {
    requiredType(DataType.ZSET);
    return getValue();
  }

  public Map<SafeString, SafeString> getHash() {
    requiredType(DataType.HASH);
    return getValue();
  }

  public int size() {
    if (value instanceof Collection) {
      return ((Collection<?>) value).size();
    }
    if (value instanceof Map) {
      return ((Map<?, ?>) value).size();
    }
    if (value instanceof SafeString) {
      return 1;
    }
    return 0;
  }

  public Instant getExpiredAt() {
    return expiredAt;
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

  public DatabaseValue expiredAt(Instant instant) {
    return new DatabaseValue(this.type, this.value, instant);
  }

  public DatabaseValue expiredAt(int ttlSeconds) {
    return new DatabaseValue(this.type, this.value, toInstant(toMillis(ttlSeconds)));
  }

  public DatabaseValue noExpire() {
    return new DatabaseValue(this.type, this.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, value);
  }

  @Override
  public boolean equals(Object obj) {
    return EQUAL.applyTo(this, obj);
  }

  @Override
  public String toString() {
    return "DatabaseValue [type=" + type + ", value=" + value + "]";
  }

  public static DatabaseValue string(byte[] array) {
    return string(new SafeString(array));
  }

  public static DatabaseValue string(String value) {
    return string(safeString(value));
  }

  public static DatabaseValue string(SafeString value) {
    return new DatabaseValue(DataType.STRING, value);
  }

  public static DatabaseValue list(Collection<SafeString> values) {
    return new DatabaseValue(DataType.LIST, values.stream().collect(toList()));
  }

  public static DatabaseValue list(SafeString... values) {
    return new DatabaseValue(DataType.LIST, Stream.of(values).collect(toList()));
  }

  public static DatabaseValue set(Collection<SafeString> values) {
    return new DatabaseValue(DataType.SET, values.stream().collect(toSet()));
  }

  public static DatabaseValue set(SafeString... values) {
    return new DatabaseValue(DataType.SET, Stream.of(values).collect(toSet()));
  }

  public static DatabaseValue zset(Collection<Entry<Double, SafeString>> values) {
    return new DatabaseValue(DataType.ZSET, values.stream().collect(toSortedSet()));
  }

  @SafeVarargs
  public static DatabaseValue zset(Entry<Double, SafeString>... values) {
    return new DatabaseValue(DataType.ZSET, Stream.of(values).collect(toSortedSet()));
  }

  public static DatabaseValue hash(Map<SafeString, SafeString> values) {
    return new DatabaseValue(DataType.HASH, requireNonNull(values));
  }

  public static DatabaseValue hash(Collection<Map.Entry<SafeString, SafeString>> values) {
    return new DatabaseValue(DataType.HASH, values.stream().collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
  }

  @SafeVarargs
  public static DatabaseValue hash(Map.Entry<SafeString, SafeString>... values) {
    return new DatabaseValue(DataType.HASH, Stream.of(values).collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
  }

  public static DatabaseValue bitset(int... ones) {
    BitSet bitSet = new BitSet();
    for (int position : ones) {
      bitSet.set(position);
    }
    return new DatabaseValue(DataType.STRING, new SafeString(bitSet.toByteArray()));
  }

  public static Map.Entry<SafeString, SafeString> entry(SafeString key, SafeString value) {
    return new AbstractMap.SimpleEntry<>(key, value);
  }

  public static Entry<Double, SafeString> score(double score, SafeString value) {
    return new SimpleEntry<>(score, value);
  }

  private static Collector<Entry<Double, SafeString>, ?, NavigableSet<Entry<Double, SafeString>>> toSortedSet() {
    return toCollection(SortedSet::new);
  }

  private long timeToLive(Instant now) {
    return Duration.between(now, expiredAt).toMillis();
  }

  private Instant toInstant(long ttlMillis) {
    return now().plusMillis(ttlMillis);
  }

  private long toMillis(int ttlSeconds) {
    return TimeUnit.SECONDS.toMillis(ttlSeconds);
  }

  @SuppressWarnings("unchecked")
  private <T> T getValue() {
    return (T) value;
  }

  private void requiredType(DataType type) {
    if (this.type != type) {
      throw new IllegalStateException("invalid type: " + type);
    }
  }
}
