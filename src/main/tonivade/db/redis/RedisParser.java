/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.redis;

import java.nio.ByteBuffer;

import tonivade.db.redis.RedisToken.ArrayRedisToken;
import tonivade.db.redis.RedisToken.ErrorRedisToken;
import tonivade.db.redis.RedisToken.IntegerRedisToken;
import tonivade.db.redis.RedisToken.StatusRedisToken;
import tonivade.db.redis.RedisToken.StringRedisToken;
import tonivade.db.redis.RedisToken.UnknownRedisToken;

public class RedisParser {

    private static final String STRING_PREFIX = "$";
    private static final String INTEGER_PREFIX = ":";
    private static final String ERROR_PREFIX = "-";
    private static final String STATUS_PREFIX = "+";
    private static final String ARRAY_PREFIX = "*";

    private final int maxLength;
    private final RedisSource source;

    public RedisParser(int maxLength, RedisSource source) {
        this.maxLength = maxLength;
        this.source = source;
    }

    public RedisToken parse() {
        String line = source.readLine();

        RedisToken token = null;

        if (line != null && !line.isEmpty()) {
            if (line.startsWith(ARRAY_PREFIX)) {
                int size = Integer.parseInt(line.substring(1));
                token = parseArray(size);
            } else if (line.startsWith(STATUS_PREFIX)) {
                token = new StatusRedisToken(line.substring(1));
            } else if (line.startsWith(ERROR_PREFIX)) {
                token = new ErrorRedisToken(line.substring(1));
            } else if (line.startsWith(INTEGER_PREFIX)) {
                token = parseIntegerToken(line);
            } else if (line.startsWith(STRING_PREFIX)) {
                token = parseStringToken(line);
            } else {
                token = new UnknownRedisToken(line);
            }
        }

        return token;
    }

    private RedisToken parseIntegerToken(String line) {
        Integer value = Integer.valueOf(line.substring(1));
        return new IntegerRedisToken(value);
    }

    private RedisToken parseStringToken(String line) {
        RedisToken token;
        int length = Integer.parseInt(line.substring(1));
        if (length > 0 && length < maxLength) {
            ByteBuffer buffer = source.readBytes(length);
            token = new StringRedisToken(new SafeString(buffer));
            source.readLine();
        } else {
            token = new StringRedisToken(SafeString.EMPTY_STRING);
        }
        return token;
    }

    private ArrayRedisToken parseArray(int size) {
        RedisArray array = new RedisArray();

        for (int i = 0 ; i < size; i++) {
            String line = source.readLine();

            if (line != null) {
                if (line.startsWith(STRING_PREFIX)) {
                    array.add(parseStringToken(line));
                } else if (line.startsWith(INTEGER_PREFIX)) {
                    array.add(parseIntegerToken(line));
                }
            }
        }

        return new ArrayRedisToken(array);
    }

}
