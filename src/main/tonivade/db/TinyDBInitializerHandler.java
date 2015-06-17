/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Netty initialization handler
 *
 * @author tomby
 *
 */
public class TinyDBInitializerHandler extends ChannelInitializer<SocketChannel> {

    private ITinyDB impl;

    public TinyDBInitializerHandler(ITinyDB impl) {
        this.impl = impl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        impl.channel(channel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        impl.disconnected(ctx);
    }

}
