package tonivade.db;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Se encarga de iniciar la conexión con el servidor
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
     * Se realiza la conexión
     */
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        impl.channel(channel);
    }

    /**
     * Se pierde la conexión
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        impl.disconnected(ctx);
    }

}
