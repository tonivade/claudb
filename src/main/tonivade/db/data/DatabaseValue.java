/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.data;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableNavigableSet;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static tonivade.db.redis.SafeString.safeString;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.stream.Collector;
import java.util.stream.Stream;

import tonivade.db.redis.SafeString;


public class DatabaseValue {

    public static final DatabaseValue EMPTY_STRING = string("");
    public static final DatabaseValue EMPTY_LIST = list();
    public static final DatabaseValue EMPTY_SET = set();
    public static final DatabaseValue EMPTY_ZSET = zset();
    public static final DatabaseValue EMPTY_HASH = hash();

    private final DataType type;

    private final Object value;

    public DatabaseValue(DataType type) {
        this(type, null);
    }

    public DatabaseValue(DataType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public DataType getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DatabaseValue other = (DatabaseValue) obj;
        if (type != other.type) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
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

    public static DatabaseValue list(Collection<SafeString> values) {
        return new DatabaseValue(
                DataType.LIST,
                unmodifiableList(values.stream().collect(toList())));
    }

    public static DatabaseValue list(SafeString ... values) {
        return new DatabaseValue(
                DataType.LIST,
                unmodifiableList(Stream.of(values).collect(toList())));
    }

    public static DatabaseValue set(Collection<SafeString> values) {
        return new DatabaseValue(
                DataType.SET,
                unmodifiableSet(values.stream().collect(toSet())));
    }

    public static DatabaseValue set(SafeString ... values) {
        return new DatabaseValue(
                DataType.SET,
                unmodifiableSet(Stream.of(values).collect(toSet())));
    }

    public static DatabaseValue zset(Collection<Entry<Double, SafeString>> values) {
        return new DatabaseValue(
                DataType.ZSET,
                unmodifiableNavigableSet(values.stream().collect(toSortedSet())));
    }

    @SafeVarargs
    public static DatabaseValue zset(Entry<Double, SafeString> ... values) {
        return new DatabaseValue(
                DataType.ZSET,
                unmodifiableNavigableSet(Stream.of(values).collect(toSortedSet())));
    }

    public static DatabaseValue hash(Collection<Entry<SafeString, SafeString>> values) {
        return new DatabaseValue(
                DataType.HASH,
                unmodifiableMap(values.stream().collect(toHash())));
    }

    @SafeVarargs
    public static DatabaseValue hash(Entry<SafeString, SafeString> ... values) {
        return new DatabaseValue(
                DataType.HASH,
                unmodifiableMap(Stream.of(values).collect(toHash())));
    }

    public static Entry<SafeString, SafeString> entry(SafeString key, SafeString value) {
        return new SimpleEntry<SafeString, SafeString>(key, value);
    }

    public static Entry<Double, SafeString> score(double score, SafeString value) {
        return new SimpleEntry<Double, SafeString>(score, value);
    }

    private static Collector<SafeString, ?, LinkedList<SafeString>> toList() {
        return toCollection(() -> new LinkedList<>());
    }

    private static Collector<SafeString, ?, LinkedHashSet<SafeString>> toSet() {
        return toCollection(() -> new LinkedHashSet<>());
    }

    private static Collector<Entry<Double, SafeString>, ?, NavigableSet<Entry<Double, SafeString>>> toSortedSet() {
        return toCollection(() -> new SortedSet());
    }

    private static Collector<Entry<SafeString, SafeString>, ?, Map<SafeString, SafeString>> toHash() {
        return toMap(Entry::getKey, Entry::getValue);
    }

}
