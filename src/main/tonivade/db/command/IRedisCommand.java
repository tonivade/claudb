package tonivade.db.command;

import tonivade.db.ITinyDB;
import tonivade.db.RedisServerState;
import tonivade.db.RedisSessionState;
import tonivade.db.data.IDatabase;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.command.IServerContext;
import tonivade.redis.command.ISession;

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
