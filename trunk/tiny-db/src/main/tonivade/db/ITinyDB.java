package tonivade.db;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

/**
 *
 * @author tomby
 *
 */
public interface ITinyDB {

    /**
     * Metodo llamado cuando se crea un nuevo canal
     *
     * @param channel
     */
    public void channel(SocketChannel channel);

    /**
     * Método llamado cuando la conexión se ha establecido y está activa
     *
     * @param ctx
     */
    public void connected(ChannelHandlerContext ctx);

    /**
     * Metodo llamado cuando se pierde la conexión
     *
     * @param ctx
     */
    public void disconnected(ChannelHandlerContext ctx);

    /**
     * Método llamado cuando se recibe un mensaje
     *
     * @param ctx
     * @param message
     */
    public void receive(ChannelHandlerContext ctx, String message);

}