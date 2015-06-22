/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.replication;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import tonivade.db.ITinyDBCallback;
import tonivade.db.TinyDBClient;
import tonivade.db.command.IServerContext;
import tonivade.db.persistence.ByteBufferInputStream;
import tonivade.db.redis.RedisToken;
import tonivade.db.redis.SafeString;

public class SlaveReplication implements ITinyDBCallback {

    private static final Logger LOGGER = Logger.getLogger(SlaveReplication.class.getName());

    private TinyDBClient client;

    private IServerContext server;

    public SlaveReplication(IServerContext server, String host, int port) {
        this.server = server;
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
        client.send("SYNC\r\n");
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onMessage(RedisToken token) {
        switch (token.getType()) {
        case STRING:
            // RDB dump
            try {
                SafeString value = token.getValue();
                server.importRDB(array(value));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "error importing RDB file", e);
            }
            break;
        case ARRAY:
            // Command
            break;

        default:
            break;
        }
    }

    private InputStream array(SafeString value)
            throws UnsupportedEncodingException {
        return new ByteBufferInputStream(value.getBytes());
    }

}
