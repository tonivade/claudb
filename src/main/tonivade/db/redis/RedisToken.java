/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.redis;


public class RedisToken {

    private static final String SEPARATOR = "=>";

    private final RedisTokenType type;

    private final Object value;

    private RedisToken(RedisTokenType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public RedisTokenType getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }

    @Override
    public String toString() {
        return type + SEPARATOR + getValue();
    }

    public static class UnknownRedisToken extends RedisToken {
        public UnknownRedisToken(String value) {
            super(RedisTokenType.UNKNOWN, value);
        }
    }

    public static class StringRedisToken extends RedisToken {
        public StringRedisToken(String value) {
            super(RedisTokenType.STRING, value);
        }
    }

    public static class StatusRedisToken extends RedisToken {
        public StatusRedisToken(String value) {
            super(RedisTokenType.STATUS, value);
        }
    }

    public static class ErrorRedisToken extends RedisToken {
        public ErrorRedisToken(String value) {
            super(RedisTokenType.ERROR, value);
        }
    }

    public static class IntegerRedisToken extends RedisToken {
        public IntegerRedisToken(Integer value) {
            super(RedisTokenType.INTEGER, value);
        }
    }

    public static class ArrayRedisToken extends RedisToken {
        public ArrayRedisToken(RedisArray value) {
            super(RedisTokenType.ARRAY, value);
        }

        public int size() {
            return this.<RedisArray>getValue().size();
        }
    }

}