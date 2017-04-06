package com.github.tonivade.tinydb.command;

import java.util.Collection;
import java.util.Optional;

import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IServerContext;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.ITinyDB;
import com.github.tonivade.tinydb.TinyDBServerState;
import com.github.tonivade.tinydb.TinyDBSessionState;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.Database;

@FunctionalInterface
public interface TinyDBCommand {
  RedisToken<?> execute(Database db, IRequest request);

  default ITinyDB getTinyDB(IServerContext server) {
    return (ITinyDB) server;
  }

  default Database getAdminDatabase(IServerContext server) {
    return getServerState(server).getAdminDatabase();
  }

  default TinyDBServerState getServerState(IServerContext server) {
    return serverState(server).orElseThrow(() -> new IllegalStateException("missing server state"));
  }

  default TinyDBSessionState getSessionState(ISession session) {
    return sessionState(session).orElseThrow(() -> new IllegalStateException("missiong session state"));
  }

  default Optional<TinyDBServerState> serverState(IServerContext server) {
    return server.getValue("state");
  }

  default Optional<TinyDBSessionState> sessionState(ISession session) {
    return session.getValue("state");
  }

  default RedisToken<?> convert(DatabaseValue value) {
    return TinyDBResponse.convertValue(value);
  }

  default RedisToken<?> convert(Collection<?> list) {
    return TinyDBResponse.convertArray(list);
  }
}
