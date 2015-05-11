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
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tonivade.db.command.CommandWrapper;
import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.Request;
import tonivade.db.command.Response;
import tonivade.db.command.impl.DecrementByCommand;
import tonivade.db.command.impl.DecrementCommand;
import tonivade.db.command.impl.DelCommand;
import tonivade.db.command.impl.EchoCommand;
import tonivade.db.command.impl.ExistsCommand;
import tonivade.db.command.impl.GetCommand;
import tonivade.db.command.impl.HashGetAllCommand;
import tonivade.db.command.impl.HashGetCommand;
import tonivade.db.command.impl.HashSetCommand;
import tonivade.db.command.impl.IncrementByCommand;
import tonivade.db.command.impl.IncrementCommand;
import tonivade.db.command.impl.MultiGetCommand;
import tonivade.db.command.impl.PingCommand;
import tonivade.db.command.impl.SetCommand;
import tonivade.db.data.Database;
import tonivade.db.redis.RedisToken;
import tonivade.db.redis.RedisToken.ArrayRedisToken;
import tonivade.db.redis.RedisToken.UnknownRedisToken;
import tonivade.db.redis.RedisTokenType;
import tonivade.db.redis.RequestDecoder;

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

    private final int port;
    private final String host;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private ServerBootstrap bootstrap;

    private TinyDBInitializerHandler acceptHandler;
    private TinyDBConnectionHandler connectionHandler;

    private final Map<String, ChannelHandlerContext> channels = new HashMap<>();

    private final Map<String, ICommand> commands = new HashMap<>();

    private final Database db = new Database();

    private ChannelFuture future;

    public TinyDB() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public TinyDB(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void init() {
        // connection
        commands.put("ping", new PingCommand());
        commands.put("echo", new CommandWrapper(new EchoCommand(), 1));

        // strings
        commands.put("get", new CommandWrapper(new GetCommand(), 1));
        commands.put("mget", new CommandWrapper(new MultiGetCommand(), 1));
        commands.put("set", new CommandWrapper(new SetCommand(), 2));
        commands.put("incr", new CommandWrapper(new IncrementCommand(), 1));
        commands.put("incrby", new CommandWrapper(new IncrementByCommand(), 2));
        commands.put("decr", new CommandWrapper(new DecrementCommand(), 1));
        commands.put("decrby", new CommandWrapper(new DecrementByCommand(), 2));

        // keys
        commands.put("del", new CommandWrapper(new DelCommand(), 1));
        commands.put("exists", new CommandWrapper(new ExistsCommand(), 1));

        // hash
        commands.put("hset", new CommandWrapper(new HashSetCommand(), 3));
        commands.put("hget", new CommandWrapper(new HashGetCommand(), 2));
        commands.put("hgetall", new CommandWrapper(new HashGetAllCommand(), 1));
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

        LOGGER.info(() -> "adapter started: " + host + ":" + port);
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

        LOGGER.info("adapter stopped");
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
        LOGGER.fine(() -> "new channel: " + sourceKey(channel));

        channel.pipeline().addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
        channel.pipeline().addLast("linDelimiter", new RequestDecoder(MAX_FRAME_SIZE));
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

        LOGGER.fine(() -> "client connected: " + sourceKey);

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

        LOGGER.fine(() -> "client disconnected: " + sourceKey);

        channels.remove(sourceKey);
    }

    /**
     * Método llamado cuando se recibe un mensaje de un cliente
     *
     * @param ctx
     * @param buffer
     */
    @Override
    public void receive(ChannelHandlerContext ctx, RedisToken<?> message) {
        String sourceKey = sourceKey(ctx.channel());

        LOGGER.finest(() -> "message received: " + sourceKey);

        ctx.writeAndFlush(processCommand(parse(message)));
    }

    private IRequest parse(RedisToken<?> message) {
        Request request = new Request();
        if (message.getType() == RedisTokenType.ARRAY) {
            ArrayRedisToken arrayToken = (ArrayRedisToken) message;
            List<String> params = new LinkedList<String>();
            for (RedisToken<?> token : arrayToken.getValue()) {
                params.add(token.getValue().toString());
            }
            request.setCommand(params.get(0));
            request.setParams(params.subList(1, params.size()));
        } else if (message.getType() == RedisTokenType.UNKNOWN) {
            UnknownRedisToken unknownToken = (UnknownRedisToken) message;
            String command = unknownToken.getValue();
            String[] params = command.split(" ");
            request.setCommand(params[0]);
            String[] array = new String[params.length - 1];
            System.arraycopy(params, 1, array, 0, array.length);
            request.setParams(Arrays.asList(array));
        }
        return request;
    }

    private String processCommand(IRequest request) {
        LOGGER.fine(() -> "received command: " + request);

        IResponse response = new Response();
        ICommand command = commands.get(request.getCommand().toLowerCase());
        if (command != null) {
            command.execute(db, request, response);
        } else {
            response.addError("ERR unknown command '" + request.getCommand() + "'");
        }
        return response.toString();
    }

    private String sourceKey(Channel channel) {
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        return remoteAddress.getHostName() + ":" + remoteAddress.getPort();
    }

    public static void main(String[] args) throws Exception {
        TinyDB db = new TinyDB();
        db.init();
        db.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> db.stop()));
    }

}
