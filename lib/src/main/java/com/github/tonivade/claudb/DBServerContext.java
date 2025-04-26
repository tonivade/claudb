/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.resp.command.ServerContext;
import com.github.tonivade.resp.protocol.RedisToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.List;

public interface DBServerContext extends ServerContext {

  int DEFAULT_PORT = 7081;
  String DEFAULT_HOST = "localhost";

  boolean isMaster();
  void setMaster(boolean master);
  void importRDB(InputStream input) throws IOException;
  void exportRDB(OutputStream output) throws IOException;
  Database getDatabase(int i);
  Database getAdminDatabase();
  void publish(String sourceKey, RedisToken message);
  List<RedisToken> getCommandsToReplicate();
  void clean(Instant now);
}
