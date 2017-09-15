/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.data;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static tonivade.equalizer.Equalizer.equalizer;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.github.tonivade.resp.protocol.SafeString;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.LinkedHashSet;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.Traversable;

public class DatabaseValue implements Serializable {

  private static final long serialVersionUID = -1001729166107392343L;

  public static final DatabaseValue EMPTY_STRING = string("");
  public static final DatabaseValue EMPTY_LIST = list();
  public static final DatabaseValue EMPTY_SET = set();
  public static final DatabaseValue EMPTY_ZSET = zset();
  public static final DatabaseValue EMPTY_HASH = hash();

  public static final DatabaseValue NULL = null;

  private final DataType type;

  private final Object value;
  private final Instant expiredAt;

  public DatabaseValue(DataType type) {
    this(type, null);
  }

  public DatabaseValue(DataType type, Object value) {
    this(type, value, null);
  }

  public DatabaseValue(DataType type, Object value, Instant expiredAt) {
    this.type = type;
    this.value = value;
    this.expiredAt = expiredAt;
  }

  public DataType getType() {
    return type;
  }

  @SuppressWarnings("unchecked")
  public <T> T getValue() {
    return (T) value;
  }
  
  public SafeString getString() {
    return getValue();
  }
  
  public List<SafeString> getList() {
    return getValue();
  }
  
  public Set<SafeString> getSet() {
    return getValue();
  }
  
  public NavigableSet<Entry<Double, SafeString>> getSortedSet() {
    return getValue();
  }
  
  public Map<SafeString, SafeString> getHash() {
    return getValue();
  }
  
  public int size() {
    return Match(value).of(Case($(instanceOf(Set.class)), Set::size),
                           Case($(instanceOf(List.class)), List::size),
                           Case($(instanceOf(Collection.class)), Collection::size),
                           Case($(instanceOf(Map.class)), Map::size),
                           Case($(), x -> 1));
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
    return equalizer(this)
        .append((one, other) -> Objects.equals(one.type, other.type))
        .append((one, other) -> Objects.equals(one.value, other.value))
        .applyTo(obj);
  }

  @Override
  public String toString() {
    return "DatabaseValue [type=" + type + ", value=" + value + "]";
  }

  public static DatabaseValue string(String value) {
    return string(safeString(value));
  }

  public static DatabaseValue string(SafeString value) {
    return new DatabaseValue(DataType.STRING, value);
  }

  public static DatabaseValue list(Traversable<SafeString> values) {
    return new DatabaseValue(DataType.LIST, requireNonNull(values).toList());
  }

  public static DatabaseValue list(Collection<SafeString> values) {
    return new DatabaseValue(DataType.LIST, requireNonNull(values).stream().collect(List.collector()));
  }

  public static DatabaseValue list(SafeString... values) {
    return new DatabaseValue(DataType.LIST, Stream.of(values).collect(List.collector()));
  }
  
  public static DatabaseValue set(Traversable<SafeString> values) {
    return new DatabaseValue(DataType.SET, requireNonNull(values).toLinkedSet());
  }

  public static DatabaseValue set(Collection<SafeString> values) {
    return new DatabaseValue(DataType.SET, 
        requireNonNull(values).stream().collect(LinkedHashSet.collector()));
  }

  public static DatabaseValue set(SafeString... values) {
    return new DatabaseValue(DataType.SET, Stream.of(values).collect(LinkedHashSet.collector()));
  }

  public static DatabaseValue zset(Collection<Entry<Double, SafeString>> values) {
    return new DatabaseValue(DataType.ZSET,
        requireNonNull(values).stream().collect(collectingAndThen(toSortedSet(), 
                                                                  Collections::unmodifiableNavigableSet)));
  }

  @SafeVarargs
  public static DatabaseValue zset(Entry<Double, SafeString>... values) {
    return new DatabaseValue(DataType.ZSET,
        Stream.of(values).collect(collectingAndThen(toSortedSet(), 
                                                    Collections::unmodifiableNavigableSet)));
  }

  public static DatabaseValue hash(Collection<Tuple2<SafeString, SafeString>> values) {
    return new DatabaseValue(DataType.HASH, requireNonNull(values).stream().collect(LinkedHashMap.collector()));
  }

  public static DatabaseValue hash(Traversable<Tuple2<SafeString, SafeString>> values) {
    return new DatabaseValue(DataType.HASH, requireNonNull(values).toLinkedMap(Tuple2::_1, Tuple2::_2));
  }

  @SafeVarargs
  public static DatabaseValue hash(Tuple2<SafeString, SafeString>... values) {
    return new DatabaseValue(DataType.HASH,
        Stream.of(values).collect(LinkedHashMap.collector()));
  }

  public static DatabaseValue bitset(int... ones) {
    BitSet bitSet = new BitSet();
    for (int position : ones) {
      bitSet.set(position);
    }
    return new DatabaseValue(DataType.STRING, new SafeString(bitSet.toByteArray()));
  }

  public static Tuple2<SafeString, SafeString> entry(SafeString key, SafeString value) {
    return Tuple.of(key, value);
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
}
