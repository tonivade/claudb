package tonivade.db.command.transaction;

import tonivade.db.ITinyDB;
import tonivade.db.TransactionState;
import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.TxIgnore;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.command.ICommand;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.command.ISession;
import tonivade.redis.command.Response;

@Command("exec")
@TxIgnore
public class ExecCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        TransactionState transaction = getTransactionIfExists(request.getSession());
        if (transaction !=  null) {
            ITinyDB server = getTinyDB(request.getServerContext());
            MetaResponse metaResponse = new MetaResponse(response);
            for (IRequest queuedRequest : transaction) {
                metaResponse.addResponse(executeCommand(server, queuedRequest));
            }
            metaResponse.build();
        } else {
            response.addError("ERR EXEC without MULTI");
        }
    }

    private Response executeCommand(ITinyDB server, IRequest queuedRequest) {
        Response response = new Response();
        ICommand command = server.getCommand(queuedRequest.getCommand());
        command.execute(queuedRequest, response);
        return response;
    }

    private TransactionState getTransactionIfExists(ISession session) {
        return session.removeValue("tx");
    }

}
