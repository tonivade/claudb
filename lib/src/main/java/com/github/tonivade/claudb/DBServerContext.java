/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.resp.command.ServerContext;
import com.github.tonivade.resp.protocol.RedisToken;
import java.time.Instant;

public interface DBServerContext extends ServerContext {

  int DEFAULT_PORT = 7081;
  String DEFAULT_HOST = "localhost";

  Database getDatabase(int i);
  Database getAdminDatabase();
  void publish(String sourceKey, RedisToken message);
  void clean(Instant now);
}
