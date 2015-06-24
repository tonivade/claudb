/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.replication;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import tonivade.db.ITinyDBCallback;
import tonivade.db.TinyDBClient;
import tonivade.db.command.ICommand;
import tonivade.db.command.IServerContext;
import tonivade.db.command.ISession;
import tonivade.db.command.Request;
import tonivade.db.command.Response;
import tonivade.db.data.IDatabase;
import tonivade.db.persistence.ByteBufferInputStream;
import tonivade.db.redis.RedisArray;
import tonivade.db.redis.RedisToken;
import tonivade.db.redis.SafeString;

public class SlaveReplication implements ITinyDBCallback {

    private static final String SYNC_COMMAND = "SYNC\r\n";

    private static final Logger LOGGER = Logger.getLogger(SlaveReplication.class.getName());

    private TinyDBClient client;

    private IServerContext server;

    private ISession session;

    public SlaveReplication(IServerContext server, ISession session, String host, int port) {
        this.server = server;
        this.session = session;
        this.client = new TinyDBClient(host, port, this);
    }

    public void start() {
        client.start();
    }

    public void stop() {
        client.stop();
    }

    @Override
    public void onConnect() {
        LOGGER.info(() -> "Connected with master");
        client.send(SYNC_COMMAND);
    }

    @Override
    public void onDisconnect() {
        LOGGER.info(() -> "Disconnected from master");
    }

    @Override
    public void onMessage(RedisToken token) {
        switch (token.getType()) {
        case STRING:
            processRDB(token);
            break;
        case ARRAY:
            processCommand(token);
            break;

        default:
            break;
        }
    }

    private void processCommand(RedisToken token) {
        RedisArray array = token.<RedisArray>getValue();

        RedisToken commandToken = array.remove(0);

        LOGGER.fine(() -> "command recieved from master: " + commandToken.getValue());

        ICommand command = server.getCommand(commandToken.getValue().toString());

        if (command != null) {
            IDatabase current = server.getDatabase(session.getCurrentDB());
            command.execute(current, request(commandToken, array), new Response());
        }
    }

    private Request request(RedisToken commandToken, RedisArray array) {
        return new Request(server, session, commandToken.getValue(), toList(array));
    }

    private List<SafeString> toList(RedisArray request) {
        List<SafeString> cmd = new LinkedList<>();
        for (RedisToken token : request) {
            cmd.add(token.<SafeString>getValue());
        }
        return cmd;
    }

    private void processRDB(RedisToken token) {
        try {
            SafeString value = token.getValue();
            server.importRDB(array(value));
            LOGGER.info(() -> "loaded RDB file from master");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "error importing RDB file", e);
        }
    }

    private InputStream array(SafeString value)
            throws UnsupportedEncodingException {
        return new ByteBufferInputStream(value.getBytes());
    }

}
