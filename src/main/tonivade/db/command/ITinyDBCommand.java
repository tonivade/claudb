package tonivade.db.command;

import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.command.IServerContext;
import com.github.tonivade.resp.command.ISession;

import tonivade.db.ITinyDB;
import tonivade.db.TinyDBServerState;
import tonivade.db.TinyDBSessionState;
import tonivade.db.data.IDatabase;

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
