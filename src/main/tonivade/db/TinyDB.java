/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import static java.util.Collections.emptyList;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import tonivade.db.command.CommandSuite;
import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IServerContext;
import tonivade.db.command.ISession;
import tonivade.db.command.Request;
import tonivade.db.command.Response;
import tonivade.db.command.Session;
import tonivade.db.data.Database;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.db.persistence.RDBInputStream;
import tonivade.db.persistence.RDBOutputStream;
import tonivade.db.redis.RedisArray;
import tonivade.db.redis.RedisToken;
import tonivade.db.redis.RedisTokenType;
import tonivade.db.redis.RequestDecoder;

/**
 * Java Redis Implementation
 *
 * @author tomby
 *
 */
public class TinyDB implements ITinyDB, IServerContext {

    private static final Logger LOGGER = Logger.getLogger(TinyDB.class.getName());

    private static final int BUFFER_SIZE = 1024 * 1024;
    private static final int MAX_FRAME_SIZE = BUFFER_SIZE * 100;

    private static final int DEFAULT_PORT = 7081;
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_DATABASES = 10;

    private final int port;
    private final String host;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private ServerBootstrap bootstrap;

    private TinyDBInitializerHandler acceptHandler;
    private TinyDBConnectionHandler connectionHandler;

    private final Map<String, ISession> clients = new HashMap<>();

    private final List<IDatabase> databases = new ArrayList<IDatabase>();
    private final IDatabase admin = new Database(new HashMap<String, DatabaseValue>());

    private final CommandSuite commands = new CommandSuite();

    private BlockingQueue<IRequest> queue = new LinkedBlockingQueue<>();

    private ChannelFuture future;

    public TinyDB() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public TinyDB(String host, int port) {
        this(host, port, DEFAULT_DATABASES);
    }

    public TinyDB(String host, int port, int databases) {
        this.host = host;
        this.port = port;
        for (int i = 0; i < databases; i++) {
            this.databases.add(new Database(new HashMap<String, DatabaseValue>()));
        }
    }

    public void start() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
        acceptHandler = new TinyDBInitializerHandler(this);
        connectionHandler = new TinyDBConnectionHandler(this);

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

        clients.clear();

        LOGGER.info("adapter stopped");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channel(SocketChannel channel) {
        LOGGER.fine(() -> "new channel: " + sourceKey(channel));

        channel.pipeline().addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
        channel.pipeline().addLast("linDelimiter", new RequestDecoder(MAX_FRAME_SIZE));
        channel.pipeline().addLast(connectionHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connected(ChannelHandlerContext ctx) {
        String sourceKey = sourceKey(ctx.channel());

        LOGGER.fine(() -> "client connected: " + sourceKey);

        clients.put(sourceKey, new Session(sourceKey, ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnected(ChannelHandlerContext ctx) {
        String sourceKey = sourceKey(ctx.channel());

        LOGGER.fine(() -> "client disconnected: " + sourceKey);

        ISession session = clients.remove(sourceKey);
        if (session != null) {
            cleanSession(session);
        }
    }

    private void cleanSession(ISession session) {
        try {
            ICommand command = commands.getCommand("unsubscribe");
            IDatabase db = databases.get(session.getCurrentDB());
            command.execute(db, new Request(this, session, "unsubscribe", emptyList()), new Response());
        } finally {
            session.destroy();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receive(ChannelHandlerContext ctx, RedisToken message) {
        String sourceKey = sourceKey(ctx.channel());

        LOGGER.finest(() -> "message received: " + sourceKey);

        IRequest request = parseMessage(sourceKey, message);
        if (request != null) {
            processCommand(request);
        }
    }

    private IRequest parseMessage(String sourceKey, RedisToken message) {
        IRequest request = null;
        if (message.getType() == RedisTokenType.ARRAY) {
            request = parseArray(sourceKey, message);
        } else if (message.getType() == RedisTokenType.UNKNOWN) {
            request = parseLine(sourceKey, message);
        }
        return request;
    }

    private Request parseLine(String sourceKey, RedisToken message) {
        String command = message.getValue();
        String[] params = command.split(" ");
        String[] array = new String[params.length - 1];
        System.arraycopy(params, 1, array, 0, array.length);
        return new Request(this, clients.get(sourceKey), params[0], Arrays.asList(array));
    }

    private Request parseArray(String sourceKey, RedisToken message) {
        List<String> params = new LinkedList<String>();
        for (RedisToken token : message.<RedisArray>getValue()) {
            params.add(token.getValue());
        }
        return new Request(this, clients.get(sourceKey), params.get(0), params.subList(1, params.size()));
    }

    private void processCommand(IRequest request) {
        LOGGER.fine(() -> "received command: " + request);

        ISession session = request.getSession();
        IDatabase db = databases.get(session.getCurrentDB());
        ICommand command = commands.getCommand(request.getCommand());
        if (command != null) {
            session.enqueue(() -> {
                Response response = new Response();
                command.execute(db, request, response);
                session.getContext().writeAndFlush(response.toString());

                queue.add(request);

                if (response.isExit()) {
                    session.getContext().close();
                }
            });
        } else {
            session.getContext().writeAndFlush("-ERR unknown command '" + request.getCommand() + "'");
        }
    }

    private String sourceKey(Channel channel) {
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        return remoteAddress.getHostName() + ":" + remoteAddress.getPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(String sourceKey, String message) {
        ISession session = clients.get(sourceKey);
        if (session != null) {
            session.getContext().writeAndFlush(message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDatabase getDatabase() {
        return admin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getClients() {
        return clients.size();
    }

    @Override
    public List<IRequest> getCommands() {
        List<IRequest> current = new LinkedList<>();
        queue.drainTo(current);
        return current;
    }

    @Override
    public void exportRDB(OutputStream output) throws IOException {
        RDBOutputStream rdb = new RDBOutputStream(output);
        rdb.preamble(6);
        for (int i = 0; i < databases.size(); i++) {
            IDatabase db = databases.get(i);
            if (db.isEmpty()) {
                rdb.select(i);
                rdb.dabatase(db);
            }
        }
        rdb.end();
    }

    @Override
    public void importRDB(InputStream input) throws IOException {
        RDBInputStream rdb = new RDBInputStream(input);

        for (Entry<Integer, IDatabase> entry : rdb.parse().entrySet()) {
            this.databases.set(entry.getKey(), entry.getValue());
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("usage: tinydb <host> <port>");

        TinyDB db = new TinyDB(parseHost(args), parsePort(args));
        db.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> db.stop()));
    }

    private static String parseHost(String[] args) {
        String host = DEFAULT_HOST;
        if (args.length > 0) {
            host = args[0];
        }
        return host;
    }

    private static int parsePort(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("worng port value: " + args[1]);
            }
        }
        return port;
    }

}
