/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.data;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import tonivade.db.redis.SafeString;

public class SortedSet implements NavigableSet<Entry<Double, SafeString>> {

    private final Map<SafeString, Double> items = new HashMap<>();

    private final NavigableSet<Entry<Double, SafeString>> scores = new TreeSet<>(
            (o1, o2) -> {
                int key = o1.getKey().compareTo(o2.getKey());
                if (key != 0) {
                    return key;
                }
                if (SafeString.EMPTY_STRING.equals(o1.getValue())) {
                    return 0;
                }
                if (SafeString.EMPTY_STRING.equals(o2.getValue())) {
                    return 0;
                }
                return o1.getValue().compareTo(o2.getValue());
            });

    @Override
    public int size() {
        return scores.size();
    }

    @Override
    public boolean isEmpty() {
        return scores.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return items.containsKey(o);
    }

    @Override
    public Iterator<Entry<Double, SafeString>> iterator() {
        return scores.iterator();
    }

    @Override
    public Object[] toArray() {
        return scores.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return scores.toArray(a);
    }

    @Override
    public boolean add(Entry<Double, SafeString> e) {
        if (!items.containsKey(e.getValue())) {
            items.put(e.getValue(), e.getKey());
            scores.add(e);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (items.containsKey(o)) {
            double score = items.remove(o);
            scores.remove(DatabaseValue.score(score, (SafeString) o));
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        boolean result = false;
        for (Object object : c) {
            result |= contains(object);
        }
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends Entry<Double, SafeString>> c) {
        boolean result = false;
        for (Entry<Double, SafeString> entry : c) {
            result |= add(entry);
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Set<SafeString> toRemove = new HashSet<>(items.keySet());
        toRemove.removeAll(c);
        boolean result = false;
        for (SafeString key : toRemove) {
            result |= remove(key);
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = false;
        for (Object object : c) {
            result |= remove(object);
        }
        return result;
    }

    @Override
    public void clear() {
        items.clear();
        scores.clear();
    }

    @Override
    public Comparator<? super Entry<Double, SafeString>> comparator() {
        return scores.comparator();
    }

    @Override
    public Entry<Double, SafeString> first() {
        return scores.first();
    }

    @Override
    public Entry<Double, SafeString> last() {
        return scores.last();
    }

    @Override
    public Entry<Double, SafeString> lower(Entry<Double, SafeString> e) {
        return scores.lower(e);
    }

    @Override
    public Entry<Double, SafeString> floor(Entry<Double, SafeString> e) {
        return scores.floor(e);
    }

    @Override
    public Entry<Double, SafeString> ceiling(Entry<Double, SafeString> e) {
        return scores.ceiling(e);
    }

    @Override
    public Entry<Double, SafeString> higher(Entry<Double, SafeString> e) {
        return scores.higher(e);
    }

    @Override
    public Entry<Double, SafeString> pollFirst() {
        return scores.pollFirst();
    }

    @Override
    public Entry<Double, SafeString> pollLast() {
        return scores.pollLast();
    }

    @Override
    public NavigableSet<Entry<Double, SafeString>> descendingSet() {
        return scores.descendingSet();
    }

    @Override
    public Iterator<Entry<Double, SafeString>> descendingIterator() {
        return scores.descendingIterator();
    }

    @Override
    public NavigableSet<Entry<Double, SafeString>> subSet(Entry<Double, SafeString> fromElement,
            boolean fromInclusive, Entry<Double, SafeString> toElement, boolean toInclusive) {
        return scores.subSet(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public NavigableSet<Entry<Double, SafeString>> headSet(Entry<Double, SafeString> toElement,
            boolean inclusive) {
        return scores.headSet(toElement, inclusive);
    }

    @Override
    public NavigableSet<Entry<Double, SafeString>> tailSet(Entry<Double, SafeString> fromElement,
            boolean inclusive) {
        return scores.tailSet(fromElement, inclusive);
    }

    @Override
    public java.util.SortedSet<Entry<Double, SafeString>> subSet(Entry<Double, SafeString> fromElement,
            Entry<Double, SafeString> toElement) {
        return scores.subSet(fromElement, toElement);
    }

    @Override
    public java.util.SortedSet<Entry<Double, SafeString>> headSet(Entry<Double, SafeString> toElement) {
        return scores.headSet(toElement);
    }

    @Override
    public java.util.SortedSet<Entry<Double, SafeString>> tailSet(Entry<Double, SafeString> fromElement) {
        return scores.tailSet(fromElement);
    }

    public double score(SafeString key) {
        if (items.containsKey(key)) {
            return items.get(key);
        }
        return Double.MIN_VALUE;
    }

    public int ranking(SafeString key) {
        if (items.containsKey(key)) {
            double score = items.get(key);

            Set<Entry<Double, SafeString>> head = scores.headSet(DatabaseValue.score(score, key));

            return head.size();
        }
        return -1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((items == null) ? 0 : items.hashCode());
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
        if (obj instanceof Set) {
            Set<?> other = (Set<?>) obj;
            if (scores != null) {
                return scores.equals(other);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return scores.toString();
    }

}
