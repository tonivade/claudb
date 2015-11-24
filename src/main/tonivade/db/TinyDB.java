/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import static tonivade.db.TinyDBConfig.withoutPersistence;
import static tonivade.redis.protocol.SafeString.safeString;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import tonivade.db.command.RedisCommandSuite;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.IDatabase;
import tonivade.db.persistence.PersistenceManager;
import tonivade.redis.RedisServer;
import tonivade.redis.command.ICommand;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.command.ISession;
import tonivade.redis.protocol.RedisToken;
import tonivade.redis.protocol.RedisToken.IntegerRedisToken;
import tonivade.redis.protocol.RedisToken.StringRedisToken;
import tonivade.redis.protocol.SafeString;

public class TinyDB extends RedisServer implements ITinyDB {

    private static final Logger LOGGER = Logger.getLogger(TinyDB.class.getName());

    private final BlockingQueue<List<RedisToken>> queue = new LinkedBlockingQueue<>();

    private Optional<PersistenceManager> persistence;

    public TinyDB() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public TinyDB(String host, int port) {
        this(host, port, withoutPersistence());
    }

    public TinyDB(String host, int port, TinyDBConfig config) {
        super(host, port, new RedisCommandSuite());
        if (config.isPersistenceActive()) {
            this.persistence = Optional.of(new PersistenceManager(this, config));
        } else {
            this.persistence = Optional.empty();
        }
        state.put("state", new RedisServerState(config.getNumDatabases()));
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();

        queue.clear();

        LOGGER.info(() -> "server stopped");
    }

    @Override
    protected void createSession(ISession session) {
        session.putValue("state", new RedisSessionState());
    }

    @Override
    protected void cleanSession(ISession session) {
        try {
            getSessionState(session).destroy();
        } finally {
            session.destroy();
        }
    }

    @Override
    protected void executeCommand(ICommand command, IRequest request, IResponse response) {
        ISession session = request.getSession();
        RedisSessionState sessionState = getSessionState(session);
        if (!isReadOnly(command)) {
            sessionState.enqueue(() -> {
                try {
                    command.execute(request, response);
                    writeResponse(session, response);

                    replication(request, command);

                    if (response.isExit()) {
                        session.getContext().close();
                    }
                } catch (RuntimeException e) {
                    LOGGER.log(Level.SEVERE, "error executing command: " + request, e);
                }
            });
        } else {
            writeResponse(session, response.addError("READONLY You can't write against a read only slave"));
        }
    }

    private boolean isReadOnly(ICommand command) {
        return !isMaster() && isReadOnlyCommand(command);
    }

    private void replication(IRequest request, ICommand command) {
        if (isReadOnlyCommand(command)) {
            List<RedisToken> array = requestToArray(request);
            if (hasSlaves()) {
                queue.add(array);
            }
            persistence.ifPresent((p) -> p.append(array));
        }
    }

    private boolean isReadOnlyCommand(ICommand command) {
        return !command.getClass().isAnnotationPresent(ReadOnly.class);
    }

    private List<RedisToken> requestToArray(IRequest request) {
        List<RedisToken> array = new LinkedList<>();
        // currentDB
        array.add(new IntegerRedisToken(getSessionState(request.getSession()).getCurrentDB()));
        // command
        array.add(new StringRedisToken(safeString(request.getCommand())));
        //params
        for (SafeString safeStr : request.getParams()) {
            array.add(new StringRedisToken(safeStr));
        }
        return array;
    }

    private RedisSessionState getSessionState(ISession session) {
        return session.<RedisSessionState>getValue("state");
    }

    @Override
    public void publish(String sourceKey, String message) {
        ISession session = clients.get(sourceKey);
        if (session != null) {
            SafeString safeString = safeString(message);
            ByteBuf buffer = session.getContext().alloc().buffer(safeString.length());
            buffer.writeBytes(safeString.getBuffer());
            session.getContext().writeAndFlush(buffer);
        }
    }

    @Override
    public IDatabase getAdminDatabase() {
        return getState().getAdminDatabase();
    }

    @Override
    public IDatabase getDatabase(int i) {
        return getState().getDatabase(i);
    }

    @Override
    public void exportRDB(OutputStream output) throws IOException {
        getState().exportRDB(output);
    }

    @Override
    public void importRDB(InputStream input) throws IOException {
        getState().importRDB(input);
    }

    @Override
    public boolean isMaster() {
        return getState().isMaster();
    }

    @Override
    public void setMaster(boolean master) {
        getState().setMaster(master);
    }

    private RedisServerState getState() {
        return getValue("state");
    }

    private boolean hasSlaves() {
        return getState().hasSlaves();
    }

    @Override
    public List<List<RedisToken>> getCommands() {
        List<List<RedisToken>> current = new LinkedList<>();
        queue.drainTo(current);
        return current;
    }

}
