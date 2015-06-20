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

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.stream.Collector;
import java.util.stream.Stream;


public class DatabaseValue {

    private final DataType type;

    private final Object value;

    public DatabaseValue(DataType type) {
        this(type, null);
    }

    public DatabaseValue(DataType type, Object value) {
        this.type = type;
        this.value = value;
    }

    /**
     * @return the type
     */
    public DataType getType() {
        return type;
    }

    /**
     * @return the value
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DatabaseValue [type=" + type + ", value=" + value + "]";
    }

    public static DatabaseValue string(String value) {
        return new DatabaseValue(DataType.STRING, value);
    }

    public static DatabaseValue list(Collection<String> values) {
        return new DatabaseValue(
                DataType.LIST,
                unmodifiableList(values.stream().collect(toList())));
    }

    public static DatabaseValue list(String ... values) {
        return new DatabaseValue(
                DataType.LIST,
                unmodifiableList(Stream.of(values).collect(toList())));
    }

    public static DatabaseValue set(Collection<String> values) {
        return new DatabaseValue(
                DataType.SET,
                unmodifiableSet(values.stream().collect(toSet())));
    }

    public static DatabaseValue set(String ... values) {
        return new DatabaseValue(
                DataType.SET,
                unmodifiableSet(Stream.of(values).collect(toSet())));
    }

    public static DatabaseValue zset(Collection<Entry<Double, String>> values) {
        return new DatabaseValue(
                DataType.ZSET,
                unmodifiableNavigableSet(values.stream().collect(toSortedSet())));
    }

    @SafeVarargs
    public static DatabaseValue zset(Entry<Double, String> ... values) {
        return new DatabaseValue(
                DataType.ZSET,
                unmodifiableNavigableSet(Stream.of(values).collect(toSortedSet())));
    }

    public static DatabaseValue hash(Collection<Entry<String, String>> values) {
        return new DatabaseValue(
                DataType.HASH,
                unmodifiableMap(values.stream().collect(toHash())));
    }

    @SafeVarargs
    public static DatabaseValue hash(Entry<String, String> ... values) {
        return new DatabaseValue(
                DataType.HASH,
                unmodifiableMap(Stream.of(values).collect(toHash())));
    }

    public static Entry<String, String> entry(String key, String value) {
        return new SimpleEntry<String, String>(key, value);
    }

    public static Entry<Double, String> score(double score, String value) {
        return new SimpleEntry<Double, String>(score, value);
    }

    private static Collector<String, ?, LinkedList<String>> toList() {
        return toCollection(() -> new LinkedList<>());
    }

    private static Collector<String, ?, LinkedHashSet<String>> toSet() {
        return toCollection(() -> new LinkedHashSet<>());
    }

    private static Collector<Entry<Double, String>, ?, NavigableSet<Entry<Double, String>>> toSortedSet() {
        return toCollection(() -> new SortedSet());
    }

    private static Collector<Entry<String, String>, ?, Map<String, String>> toHash() {
        return toMap(Entry::getKey, Entry::getValue);
    }

}
