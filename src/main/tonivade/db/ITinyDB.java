/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import tonivade.db.data.IDatabase;
import tonivade.redis.command.IServerContext;
import tonivade.redis.protocol.RedisToken;

public interface ITinyDB extends IServerContext {

    int DEFAULT_PORT = 7081;
    String DEFAULT_HOST = "localhost";

    boolean isMaster();

    void setMaster(boolean master);

    void importRDB(InputStream input) throws IOException;

    void exportRDB(OutputStream output) throws IOException;

    IDatabase getDatabase(int i);

    IDatabase getAdminDatabase();

    void publish(String sourceKey, RedisToken message);

    List<List<RedisToken>> getCommandsToReplicate();
}