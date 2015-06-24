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

    private RedisSource source;

    public RedisParser(RedisSource source) {
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
                Integer value = Integer.valueOf(line.substring(1));
                token = new IntegerRedisToken(value);
            } else if (line.startsWith(STRING_PREFIX)) {
                int length = Integer.parseInt(line.substring(1));
                ByteBuffer bulk = source.readBytes(length);
                token = new StringRedisToken(new SafeString(bulk));
                source.readLine();
            } else {
                token = new UnknownRedisToken(line);
            }
        }

        return token;
    }

    private ArrayRedisToken parseArray(int size) {
        RedisArray array = new RedisArray();

        for (int i = 0 ; i < size; i++) {
            String line = source.readLine();

            if (line != null) {
                if (line.startsWith(STRING_PREFIX)) {
                    int length = Integer.parseInt(line.substring(1));
                    ByteBuffer bulk = source.readBytes(length);
                    array.add(new StringRedisToken(new SafeString(bulk)));
                    source.readLine();
                } else if (line.startsWith(INTEGER_PREFIX)) {
                    // integer
                    Integer value = Integer.valueOf(line.substring(1));
                    array.add(new IntegerRedisToken(value));
                }
            }
        }

        return new ArrayRedisToken(array);
    }

}
