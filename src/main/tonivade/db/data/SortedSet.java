/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public class SortedSet implements Set<Entry<Float, String>> {

    private Map<String, Float> items = new HashMap<>();

    private NavigableSet<Entry<Float, String>> scores = new TreeSet<>(
            (o1, o2) -> {
                int key = o1.getKey().compareTo(o2.getKey());
                return key != 0 ? key : o1.getValue().compareTo(o2.getValue());
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
    public Iterator<Entry<Float, String>> iterator() {
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
    public boolean add(Entry<Float, String> e) {
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
            float score = items.remove(o);
            scores.remove(DatabaseValue.score(score, (String) o));
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Entry<Float, String>> c) {
        boolean result = false;
        for (Entry<Float, String> entry : c) {
            result |= add(entry);
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
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

    public float score(String key) {
        if (items.containsKey(key)) {
            return items.get(key);
        }
        return Float.MIN_VALUE;
    }

    public int ranking(String key) {
        if (items.containsKey(key)) {
            float score = items.get(key);

            Set<Entry<Float, String>> head = scores.headSet(DatabaseValue.score(score, key));

            return head.size();
        }
        return -1;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((items == null) ? 0 : items.hashCode());
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
