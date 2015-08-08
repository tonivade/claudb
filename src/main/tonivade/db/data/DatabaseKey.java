/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.data;

import static tonivade.db.redis.SafeString.safeString;
import tonivade.db.redis.SafeString;

public class DatabaseKey implements Comparable<DatabaseKey> {

    private Long expiredAt;
    private SafeString value;

    public DatabaseKey(SafeString value) {
        this(value, 0);
    }

    public DatabaseKey(SafeString value, long ttl) {
        super();
        this.value = value;
        if (ttl > 0) {
            this.expiredAt = System.currentTimeMillis() + ttl;
        }
    }

    public SafeString getValue() {
        return value;
    }

    public boolean isExpired() {
        if (expiredAt != null) {
            long now = System.currentTimeMillis();
            return now > expiredAt;
        }
        return false;
    }

    @Override
    public int compareTo(DatabaseKey o) {
        return value.compareTo(o.getValue());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        DatabaseKey other = (DatabaseKey) obj;
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
        return value.toString();
    }

    public static DatabaseKey safeKey(SafeString str) {
        return new DatabaseKey(str);
    }

    public static DatabaseKey ttlKey(SafeString str, long ttl) {
        return new DatabaseKey(str, ttl);
    }

    public static DatabaseKey safeKey(String str) {
        return new DatabaseKey(safeString(str));
    }

    public static DatabaseKey ttlKey(String str, long ttl) {
        return new DatabaseKey(safeString(str), ttl);
    }

}
