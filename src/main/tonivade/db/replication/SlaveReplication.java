/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.replication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import tonivade.db.ITinyDBCallback;
import tonivade.db.TinyDBClient;
import tonivade.db.command.IServerContext;
import tonivade.db.redis.RedisToken;

public class SlaveReplication implements ITinyDBCallback {

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
                String value = token.getValue();
                server.importRDB(array(value));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            break;
        case ARRAY:
            // Command
            break;

        default:
            break;
        }
    }

    private ByteArrayInputStream array(String value)
            throws UnsupportedEncodingException {
        return new ByteArrayInputStream(value.getBytes("UTF-8"));
    }

}
