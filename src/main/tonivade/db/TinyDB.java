package tonivade.db;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import tonivade.db.command.CommandWrapper;
import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.Request;
import tonivade.db.command.Response;
import tonivade.db.command.impl.EchoCommand;
import tonivade.db.command.impl.ExistsCommand;
import tonivade.db.command.impl.GetCommand;
import tonivade.db.command.impl.PingCommand;
import tonivade.db.command.impl.SetCommand;
import tonivade.db.data.Database;

/**
 * Java Redis Implementation
 *
 * @author tomby
 *
 */
public class TinyDB implements ITinyDB {

    private static final Logger LOGGER = Logger.getLogger(TinyDB.class.getName());

    // Buffer size
    private static final int BUFFER_SIZE = 1024 * 1024;
    // Max message size
    private static final int MAX_FRAME_SIZE = BUFFER_SIZE * 100;
    // Default port number
    private static final int DEFAULT_PORT = 7081;
    // Default host name
    private static final String DEFAULT_HOST = "localhost";

    private int port;
    private String host;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private ServerBootstrap bootstrap;

    private TinyDBInitializerHandler acceptHandler;
    private TinyDBConnectionHandler connectionHandler;

    private Map<String, ChannelHandlerContext> channels = new HashMap<>();

    private Map<String, ICommand> commands = new HashMap<>();

    private Database db = new Database();

    private ChannelFuture future;

    public TinyDB() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public TinyDB(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void init() {
        commands.put("ping", new PingCommand());
        commands.put("set", new CommandWrapper(new SetCommand(), 3));
        commands.put("get", new CommandWrapper(new GetCommand(), 2));
        commands.put("echo", new CommandWrapper(new EchoCommand(), 2));
        commands.put("exists", new CommandWrapper(new ExistsCommand(), 2));
    }

    public void start() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
        acceptHandler = new TinyDBInitializerHandler(this);
        connectionHandler = new TinyDBConnectionHandler(this);
        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(acceptHandler)
             .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
             .option(ChannelOption.SO_RCVBUF, BUFFER_SIZE)
             .option(ChannelOption.SO_SNDBUF, BUFFER_SIZE)
             .childOption(ChannelOption.SO_KEEPALIVE, true)
             .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            // Bind and start to accept incoming connections.
            future = bootstrap.bind(host, port);
        } catch(RuntimeException e) {
            throw new TinyDBException(e);
        }

        LOGGER.log(Level.INFO, "adapter started");
    }

    public void stop() {
        try {
            if (future != null) {
                future.channel().close();
            }
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

        LOGGER.log(Level.INFO, "adapter stopped");
    }

    /**
     * Metodo llamado cuando se establece la conexión con un nuevo cliente.
     *
     * Se inicializa el pipeline, añadiendo el handler
     *
     * @param channel
     */
    @Override
    public void channel(SocketChannel channel) {
        LOGGER.log(Level.INFO, "new channel: {0}", sourceKey(channel));

        channel.pipeline().addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
        channel.pipeline().addLast("linDelimiter",
                new DelimiterBasedFrameDecoder(MAX_FRAME_SIZE, true, Delimiters.lineDelimiter()));
        channel.pipeline().addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
        channel.pipeline().addLast(connectionHandler);
    }

    /**
     * Método llamado cuando el canal está activo.
     *
     * Se notifica la conexión de un nuevo cliente.
     *
     * @param ctx
     */
    @Override
    public void connected(ChannelHandlerContext ctx) {
        String sourceKey = sourceKey(ctx.channel());

        LOGGER.log(Level.INFO, "client connected: {0}", sourceKey);

        channels.put(sourceKey, ctx);
    }

    /**
     * Metodo llamado cuando se pierde la conexión con un cliente
     *
     * @param ctx
     */
    @Override
    public void disconnected(ChannelHandlerContext ctx) {
        String sourceKey = sourceKey(ctx.channel());

        LOGGER.log(Level.INFO, "client disconnected: {0}", sourceKey);

        channels.remove(sourceKey);
    }

    /**
     * Método llamado cuando se recibe un mensaje de un cliente
     *
     * @param ctx
     * @param buffer
     */
    @Override
    public void receive(ChannelHandlerContext ctx, String message) {
        String sourceKey = sourceKey(ctx.channel());

        LOGGER.log(Level.FINEST, "message received: {0}", sourceKey);

        ctx.writeAndFlush(processCommand(parse(message)));
    }

    private IRequest parse(String message) {
        Request request = new Request();
        String[] params = message.split(" ");
        request.setCommand(params[0]);
        request.setParams(Arrays.asList(params));
        return request;
    }

    private String processCommand(IRequest request) {
        String cmd = request.getCommand();
        LOGGER.log(Level.INFO, "command: {0}", cmd);

        IResponse response = new Response();
        ICommand command = commands.get(cmd);
        if (command != null) {
            command.execute(db, request, response);
        } else {
            response.addError("ERR unknown command '" + cmd + "'");
        }
        return response.toString();
    }

    private String sourceKey(Channel channel) {
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        return remoteAddress.getHostName() + ":" + remoteAddress.getPort();
    }

    public static void main(String[] args) {
        TinyDB db = new TinyDB();
        db.init();
        db.start();
    }

}
