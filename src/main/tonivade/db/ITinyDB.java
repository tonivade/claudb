/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import tonivade.db.redis.RedisToken;

/**
 * Server interface
 *
 * @author tomby
 *
 */
public interface ITinyDB {

    public static final int DEFAULT_PORT = 7081;
    public static final String DEFAULT_HOST = "localhost";

    /**
     * When a new channel is created, and the server has to prepare the pipeline
     *
     * @param channel
     */
    public void channel(SocketChannel channel);

    /**
     * When a new client is connected
     *
     * @param ctx
     */
    public void connected(ChannelHandlerContext ctx);

    /**
     * When a client is disconnected
     *
     * @param ctx
     */
    public void disconnected(ChannelHandlerContext ctx);

    /**
     * When a message is received
     *
     * @param ctx
     * @param message
     */
    public void receive(ChannelHandlerContext ctx, RedisToken message);

}