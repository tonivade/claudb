/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.data;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import tonivade.db.redis.SafeString;

public class DatabaseKey implements Comparable<DatabaseKey> {

    private final Long expiredAt;
    private final SafeString value;

    public DatabaseKey(SafeString value, Long expiredAt) {
        super();
        this.value = value;
        this.expiredAt = expiredAt;
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

    public long timeToLive() {
        if (expiredAt != null) {
            long ttl = expiredAt - System.currentTimeMillis();
            return ttl < 0 ? -2 : ttl;
        }
        return -1;
    }

    public Long expiredAt() {
        return expiredAt;
    }

    @Override
    public int compareTo(DatabaseKey o) {
        return value.compareTo(o.getValue());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(value).toHashCode();
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
        return new EqualsBuilder().append(this.value, other.value).isEquals();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public static DatabaseKey safeKey(SafeString str) {
        return new DatabaseKey(str, null);
    }

    public static DatabaseKey safeKey(SafeString str, long ttlMillis) {
        return new DatabaseKey(str, System.currentTimeMillis() + ttlMillis);
    }

    public static DatabaseKey safeKey(SafeString str, int ttlSeconds) {
        return safeKey(str, TimeUnit.SECONDS.toMillis(ttlSeconds));
    }

}
