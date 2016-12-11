/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import static java.util.stream.Collectors.toList;
import static tonivade.db.TinyDBConfig.withoutPersistence;

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

import com.github.tonivade.resp.RedisServer;
import com.github.tonivade.resp.command.ICommand;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.resp.protocol.RedisToken;

import tonivade.db.command.TinyDBCommandSuite;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.IDatabase;
import tonivade.db.persistence.PersistenceManager;

public class TinyDB extends RedisServer implements ITinyDB {

    private static final Logger LOGGER = Logger.getLogger(TinyDB.class.getName());

    private final BlockingQueue<List<RedisToken>> queue = new LinkedBlockingQueue<>();

    private final Optional<PersistenceManager> persistence;

    public TinyDB() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public TinyDB(String host, int port) {
        this(host, port, withoutPersistence());
    }

    public TinyDB(String host, int port, TinyDBConfig config) {
        super(host, port, new TinyDBCommandSuite());
        if (config.isPersistenceActive()) {
            this.persistence = Optional.of(new PersistenceManager(this, config));
        } else {
            this.persistence = Optional.empty();
        }
        putValue("state", new TinyDBServerState(config.getNumDatabases()));
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
    public List<List<RedisToken>> getCommandsToReplicate() {
        List<List<RedisToken>> current = new LinkedList<>();
        queue.drainTo(current);
        return current;
    }

    @Override
    public void publish(String sourceKey, RedisToken message) {
        ISession session = getSession(sourceKey);
        if (session != null) {
            session.publish(message);
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

    @Override
    protected void createSession(ISession session) {
        session.putValue("state", new TinyDBSessionState());
    }

    @Override
    protected void cleanSession(ISession session) {
        session.destroy();
    }

    @Override
    protected void executeCommand(ICommand command, IRequest request, IResponse response) {
        if (!isReadOnly(request.getCommand())) {
            try {
                command.execute(request, response);

                replication(request, command);
            } catch (RuntimeException e) {
                LOGGER.log(Level.SEVERE, "error executing command: " + request, e);
            }
        } else {
            response.addError("READONLY You can't write against a read only slave");
        }
    }

    private boolean isReadOnly(String command) {
        return !isMaster() && !isReadOnlyCommand(command);
    }

    private void replication(IRequest request, ICommand command) {
        if (!isReadOnlyCommand(request.getCommand())) {
            List<RedisToken> array = requestToArray(request);
            if (hasSlaves()) {
                queue.add(array);
            }
            persistence.ifPresent((p) -> p.append(array));
        }
    }

    private boolean isReadOnlyCommand(String command) {
        return getCommands().isPresent(command, ReadOnly.class);
    }

    private List<RedisToken> requestToArray(IRequest request) {
        List<RedisToken> array = new LinkedList<>();
        array.add(currentDbToken(request));
        array.add(commandToken(request));
        array.addAll(paramTokens(request));
        return array;
    }

    private RedisToken commandToken(IRequest request) {
        return RedisToken.string(request.getCommand());
    }

    private RedisToken currentDbToken(IRequest request) {
        return RedisToken.string(String.valueOf(getCurrentDB(request)));
    }

    private int getCurrentDB(IRequest request) {
        return getSessionState(request.getSession()).getCurrentDB();
    }

    private List<RedisToken> paramTokens(IRequest request) {
        return request.getParams().stream().map(RedisToken::string).collect(toList());
    }

    private TinyDBSessionState getSessionState(ISession session) {
        return session.<TinyDBSessionState>getValue("state");
    }

    private TinyDBServerState getState() {
        return getValue("state");
    }

    private boolean hasSlaves() {
        return getState().hasSlaves();
    }

}
