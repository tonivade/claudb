/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.data;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.function.BiFunction;

public class Database implements IDatabase, Runnable {

    private final StampedLock lock = new StampedLock();

    private final NavigableMap<DatabaseKey, DatabaseValue> cache;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public Database() {
        this(new TreeMap<>());
    }

    public Database(NavigableMap<DatabaseKey, DatabaseValue> cache) {
        this.cache = cache;
        this.executor.scheduleAtFixedRate(this, 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        Set<DatabaseKey> toRemove = keySet().stream().filter(DatabaseKey::isExpired).collect(toSet());

        if (!toRemove.isEmpty()) {
            long stamp = lock.writeLock();
            try {
                for (DatabaseKey key : toRemove) {
                    cache.remove(key);
                }
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

    @Override
    public int size() {
        long stamp = lock.readLock();
        try {
            return cache.size();
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public boolean isEmpty() {
        long stamp = lock.readLock();
        try {
            return cache.isEmpty();
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public boolean containsKey(Object key) {
        long stamp = lock.readLock();
        try {
            return cache.containsKey(key);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        long stamp = lock.readLock();
        try {
            return cache.containsValue(value);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public DatabaseValue get(Object key) {
        Entry<DatabaseKey, DatabaseValue> entry = null;

        if (key instanceof DatabaseKey) {
            entry = getEntry((DatabaseKey) key);
        }

        return (entry != null && !entry.getKey().isExpired()) ? entry.getValue() : null;
    }

    private Entry<DatabaseKey, DatabaseValue> getEntry(DatabaseKey key) {
        Entry<DatabaseKey, DatabaseValue> entry;

        long optimistic = lock.tryOptimisticRead();
        entry = cache.ceilingEntry(key);
        if (!lock.validate(optimistic)) {
            long stamp = lock.readLock();
            try {
                entry = cache.ceilingEntry(key);
            } finally {
                lock.unlockRead(stamp);
            }
        }

        return entry != null && entry.getKey().equals(key) ? entry : null;
    }

    @Override
    public DatabaseValue put(DatabaseKey key, DatabaseValue value) {
        long stamp = lock.writeLock();
        try {
            return cache.put(key, value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public DatabaseValue remove(Object key) {
        long stamp = lock.writeLock();
        try {
            return cache.remove(key);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public void putAll(Map<? extends DatabaseKey, ? extends DatabaseValue> m) {
        long stamp = lock.writeLock();
        try {
            cache.putAll(m);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public void clear() {
        long stamp = lock.writeLock();
        try {
            cache.clear();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public Set<DatabaseKey> keySet() {
        long stamp = lock.readLock();
        try {
            return unmodifiableSet(cache.keySet().stream().collect(toSet()));
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public Collection<DatabaseValue> values() {
        long stamp = lock.readLock();
        try {
            return unmodifiableList(cache.values().stream().collect(toList()));
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public Set<java.util.Map.Entry<DatabaseKey, DatabaseValue>> entrySet() {
        long stamp = lock.readLock();
        try {
            return cache.entrySet().stream().map((entry) -> new SimpleEntry<DatabaseKey, DatabaseValue>(entry.getKey(), entry.getValue())).collect(toSet());
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public DatabaseValue putIfAbsent(DatabaseKey key, DatabaseValue value) {
        long stamp = lock.writeLock();
        try {
            return cache.putIfAbsent(key, value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public DatabaseValue merge(
            DatabaseKey key,
            DatabaseValue value,
            BiFunction<? super DatabaseValue, ? super DatabaseValue, ? extends DatabaseValue> remappingFunction) {
        long stamp = lock.writeLock();
        try {
            return cache.merge(key, value, remappingFunction);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public boolean isType(DatabaseKey key, DataType type) {
        long stamp = lock.readLock();
        try {
            return cache.getOrDefault(key, new DatabaseValue(type)).getType() == type;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public boolean rename(DatabaseKey from, DatabaseKey to) {
        long stamp = lock.writeLock();
        try {
            DatabaseValue value = cache.remove(from);
            if (value != null) {
                cache.put(to, value);
                return true;
            }
            return false;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public DatabaseKey overrideKey(DatabaseKey key) {
        Entry<DatabaseKey, DatabaseValue> entry = getEntry(key);

        if (entry != null) {
            long stamp = lock.writeLock();
            try {
                cache.put(key, entry.getValue());
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        return entry != null ? entry.getKey() : null;
    }

    @Override
    public DatabaseKey getKey(DatabaseKey key) {
        Entry<DatabaseKey, DatabaseValue> entry = getEntry(key);
        return entry != null ? entry.getKey() : null;
    }

}
