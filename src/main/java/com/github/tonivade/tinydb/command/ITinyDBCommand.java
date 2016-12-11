package com.github.tonivade.tinydb.command;

import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.command.IServerContext;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.tinydb.ITinyDB;
import com.github.tonivade.tinydb.TinyDBServerState;
import com.github.tonivade.tinydb.TinyDBSessionState;
import com.github.tonivade.tinydb.data.IDatabase;

public interface ITinyDBCommand {

    void execute(IDatabase db, IRequest request, IResponse response);

    default ITinyDB getTinyDB(IServerContext server) {
        return (ITinyDB) server;
    }

    default IDatabase getAdminDatabase(IServerContext server) {
        return getServerState(server).getAdminDatabase();
    }

    default TinyDBServerState getServerState(IServerContext server) {
        return server.getValue("state");
    }

    default TinyDBSessionState getSessionState(ISession session) {
        return session.getValue("state");
    }

}
