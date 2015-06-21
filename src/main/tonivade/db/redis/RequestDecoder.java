/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.redis;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.nio.charset.Charset;

import tonivade.db.redis.RedisToken.ArrayRedisToken;
import tonivade.db.redis.RedisToken.ErrorRedisToken;
import tonivade.db.redis.RedisToken.IntegerRedisToken;
import tonivade.db.redis.RedisToken.StatusRedisToken;
import tonivade.db.redis.RedisToken.StringRedisToken;
import tonivade.db.redis.RedisToken.UnknownRedisToken;

public class RequestDecoder extends LineBasedFrameDecoder {

    private static final String STRING_PREFIX = "$";
    private static final String INTEGER_PREFIX = ":";
    private static final String ERROR_PREFIX = "-";
    private static final String STATUS_PREFIX = "+";
    private static final String ARRAY_PREFIX = "*";

    public RequestDecoder(int maxLength) {
        super(maxLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        return parseResponse(ctx, buffer);
    }

    private String readLine(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        ByteBuf readLine = (ByteBuf) super.decode(ctx, buffer);

        if (readLine != null) {
            try {
                return readLine.toString(Charset.forName("UTF-8"));
            } finally {
                readLine.release();
            }
        } else {
            return null;
        }
    }

    private RedisToken parseResponse(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        String line = readLine(ctx, buffer);

        RedisToken token = null;

        if (line != null) {
            if (line.startsWith(ARRAY_PREFIX)) {
                int size = Integer.parseInt(line.substring(1));
                token = parseArray(ctx, buffer, size);
            } else if (line.startsWith(STATUS_PREFIX)) {
                token = new StatusRedisToken(line.substring(1));
            } else if (line.startsWith(ERROR_PREFIX)) {
                token = new ErrorRedisToken(line.substring(1));
            } else if (line.startsWith(INTEGER_PREFIX)) {
                Integer value = Integer.valueOf(line.substring(1));
                token = new IntegerRedisToken(value);
            } else if (line.startsWith(STRING_PREFIX)) {
                int length = Integer.parseInt(line.substring(1));
                ByteBuf bulk = buffer.readBytes(length);
                token = new StringRedisToken(new SafeString(bulk.array()));
                readLine(ctx, buffer);
            } else {
                token = new UnknownRedisToken(line);
            }
        }

        return token;
    }

    private ArrayRedisToken parseArray(ChannelHandlerContext ctx, ByteBuf buffer, int size) throws Exception {
        RedisArray response = new RedisArray();

        for (int i = 0 ; i < size; i++) {
            String line = readLine(ctx, buffer);

            if (line != null) {
                if (line.startsWith(STRING_PREFIX)) {
                    int length = Integer.parseInt(line.substring(1));
                    ByteBuf bulk = buffer.readBytes(length);
                    response.add(new StringRedisToken(new SafeString(bulk.array())));
                    readLine(ctx, buffer);
                } else if (line.startsWith(INTEGER_PREFIX)) {
                    // integer
                    Integer value = Integer.valueOf(line.substring(1));
                    response.add(new IntegerRedisToken(value));
                }
            }
        }

        return new ArrayRedisToken(response);
    }

}
