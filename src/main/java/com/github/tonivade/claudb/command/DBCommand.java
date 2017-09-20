/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command;

import java.util.Collection;
import java.util.Optional;

import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.ServerContext;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.DBServerState;
import com.github.tonivade.claudb.DBSessionState;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;

import io.vavr.collection.Traversable;

@FunctionalInterface
public interface DBCommand {
  RedisToken execute(Database db, Request request);

  default DBServerContext getClauDB(ServerContext server) {
    return (DBServerContext) server;
  }

  default Database getAdminDatabase(ServerContext server) {
    return getServerState(server).getAdminDatabase();
  }

  default DBServerState getServerState(ServerContext server) {
    return serverState(server).orElseThrow(() -> new IllegalStateException("missing server state"));
  }

  default DBSessionState getSessionState(Session session) {
    return sessionState(session).orElseThrow(() -> new IllegalStateException("missiong session state"));
  }

  default Optional<DBServerState> serverState(ServerContext server) {
    return server.getValue("state");
  }

  default Optional<DBSessionState> sessionState(Session session) {
    return session.getValue("state");
  }

  default RedisToken convert(DatabaseValue value) {
    return DBResponse.convertValue(value);
  }

  default RedisToken convert(Collection<?> list) {
    return DBResponse.convertArray(list);
  }

  default RedisToken convert(Traversable<?> list) {
    return DBResponse.convertArray(list.toJavaList());
  }
}
