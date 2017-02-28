package com.github.tonivade.tinydb.command;

import java.util.Optional;

import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.command.IServerContext;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.tinydb.ITinyDB;
import com.github.tonivade.tinydb.TinyDBServerState;
import com.github.tonivade.tinydb.TinyDBSessionState;
import com.github.tonivade.tinydb.data.IDatabase;

@FunctionalInterface
public interface ITinyDBCommand {
  void execute(IDatabase db, IRequest request, IResponse response);

  default ITinyDB getTinyDB(IServerContext server) {
    return (ITinyDB) server;
  }

  default IDatabase getAdminDatabase(IServerContext server) {
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
}
