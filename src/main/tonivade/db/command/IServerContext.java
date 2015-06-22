/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import tonivade.db.data.IDatabase;
import tonivade.db.redis.RedisArray;

public interface IServerContext {

    public int getPort();

    public int getClients();

    public void publish(String destination, String message);

    public IDatabase getAdminDatabase();

    public IDatabase getDatabase(int i);

    public void exportRDB(OutputStream output) throws IOException;

    public void importRDB(InputStream input) throws IOException;

    public List<RedisArray> getCommands();

    public ICommand getCommand(String name);

}
