package tonivade.db.command;

import tonivade.db.ITinyDB;
import tonivade.db.RedisServerState;
import tonivade.db.RedisSessionState;
import tonivade.db.data.IDatabase;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;
import tonivade.server.command.IServerContext;
import tonivade.server.command.ISession;

public interface IRedisCommand {

    void execute(IDatabase db, IRequest request, IResponse response);

    default ITinyDB getTinyDB(IServerContext server) {
        return (ITinyDB) server;
    }

    default IDatabase getAdminDatabase(IServerContext server) {
        return getServerState(server).getAdminDatabase();
    }

    default RedisServerState getServerState(IServerContext server) {
        return server.getValue("state");
    }

    default RedisSessionState getSessionState(ISession session) {
        return session.getValue("state");
    }

}
