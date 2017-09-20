package com.github.tonivade.claudb.command;

import java.util.Collection;
import java.util.Optional;

import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.ServerContext;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.TinyDBServerContext;
import com.github.tonivade.claudb.TinyDBServerState;
import com.github.tonivade.claudb.TinyDBSessionState;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;

import io.vavr.collection.Traversable;

@FunctionalInterface
public interface TinyDBCommand {
  RedisToken execute(Database db, Request request);

  default TinyDBServerContext getTinyDB(ServerContext server) {
    return (TinyDBServerContext) server;
  }

  default Database getAdminDatabase(ServerContext server) {
    return getServerState(server).getAdminDatabase();
  }

  default TinyDBServerState getServerState(ServerContext server) {
    return serverState(server).orElseThrow(() -> new IllegalStateException("missing server state"));
  }

  default TinyDBSessionState getSessionState(Session session) {
    return sessionState(session).orElseThrow(() -> new IllegalStateException("missiong session state"));
  }

  default Optional<TinyDBServerState> serverState(ServerContext server) {
    return server.getValue("state");
  }

  default Optional<TinyDBSessionState> sessionState(Session session) {
    return session.getValue("state");
  }

  default RedisToken convert(DatabaseValue value) {
    return TinyDBResponse.convertValue(value);
  }

  default RedisToken convert(Collection<?> list) {
    return TinyDBResponse.convertArray(list);
  }

  default RedisToken convert(Traversable<?> list) {
    return TinyDBResponse.convertArray(list.toJavaList());
  }
}
