/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import tonivade.db.redis.RedisToken;

public interface ITinyDB {

    public static final int DEFAULT_PORT = 7081;
    public static final String DEFAULT_HOST = "localhost";

    public void channel(SocketChannel channel);

    public void connected(ChannelHandlerContext ctx);

    public void disconnected(ChannelHandlerContext ctx);

    public void receive(ChannelHandlerContext ctx, RedisToken message);

}