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

import tonivade.db.ITinyDB;
import tonivade.db.persistence.ByteBufferInputStream;
import tonivade.server.ITinyCallback;
import tonivade.server.TinyClient;
import tonivade.server.command.ICommand;
import tonivade.server.command.ISession;
import tonivade.server.command.Request;
import tonivade.server.command.Response;
import tonivade.server.protocol.RedisToken;
import tonivade.server.protocol.SafeString;

public class SlaveReplication implements ITinyCallback {

    private static final String SYNC_COMMAND = "SYNC\r\n";

    private static final Logger LOGGER = Logger.getLogger(SlaveReplication.class.getName());

    private final TinyClient client;

    private final ITinyDB server;

    private final ISession session;

    public SlaveReplication(ITinyDB server, ISession session, String host, int port) {
        this.server = server;
        this.session = session;
        this.client = new TinyClient(host, port, this);
    }

    public void start() {
        client.start();
        server.setMaster(false);
    }

    public void stop() {
        client.stop();
        server.setMaster(true);
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
        List<RedisToken> array = token.<List<RedisToken>>getValue();

        RedisToken commandToken = array.remove(0);

        LOGGER.fine(() -> "command recieved from master: " + commandToken.getValue());

        ICommand command = server.getCommand(commandToken.getValue().toString());

        if (command != null) {
            command.execute(request(commandToken, array), new Response());
        }
    }

    private Request request(RedisToken commandToken, List<RedisToken> array) {
        return new Request(server, session, commandToken.getValue(), arrayToList(array));
    }

    private List<SafeString> arrayToList(List<RedisToken> request) {
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
