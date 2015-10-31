/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.redis;

import java.io.IOError;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LineBasedFrameDecoder;

public class RequestDecoder extends LineBasedFrameDecoder {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final int maxLength;

    public RequestDecoder(int maxLength) {
        super(maxLength);
        this.maxLength = maxLength;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        return parseResponse(ctx, buffer);
    }

    private String readLine(ChannelHandlerContext ctx, ByteBuf buffer) {
        try {
            ByteBuf readLine = (ByteBuf) super.decode(ctx, buffer);

            if (readLine != null) {
                try {
                    return readLine.toString(DEFAULT_CHARSET);
                } finally {
                    readLine.release();
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new IOError(e);
        }
    }

    private ByteBuffer readBytes(ByteBuf buffer, int size) {
        return buffer.readBytes(size).nioBuffer();
    }

    private RedisToken parseResponse(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        RedisParser parser = new RedisParser(maxLength, new RedisSource() {
            @Override
            public ByteBuffer readBytes(int size) {
                return RequestDecoder.this.readBytes(buffer, size);
            }

            @Override
            public String readLine() {
                return RequestDecoder.this.readLine(ctx, buffer);
            }
        });

        return parser.parse();
    }
}
