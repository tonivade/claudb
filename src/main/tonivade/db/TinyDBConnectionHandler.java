package tonivade.db;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

import tonivade.db.redis.RedisToken;

/**
 * Se encarga de gestionar la conexión con el servidor
 *
 * @author tomby
 *
 */
@Sharable
public class TinyDBConnectionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = Logger.getLogger(TinyDBConnectionHandler.class.getName());

    private final ITinyDB impl;

    public TinyDBConnectionHandler(ITinyDB impl) {
        this.impl = impl;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        impl.connected(ctx);
    }

    /**
     * Se recibe un mensaje
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            impl.receive(ctx, (RedisToken<?>) msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * Se ha cerrado el canal
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.log(Level.SEVERE, "channel inactive");
        impl.disconnected(ctx);
        ctx.close();
    }

    /**
     * Se produce una excepción
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.log(Level.SEVERE, "uncaught exception", cause);
        impl.disconnected(ctx);
        ctx.close();
    }

}