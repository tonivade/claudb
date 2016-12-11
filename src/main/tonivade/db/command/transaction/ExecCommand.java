package tonivade.db.command.transaction;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.ICommand;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.resp.command.Response;

import tonivade.db.ITinyDB;
import tonivade.db.TransactionState;
import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.TxIgnore;
import tonivade.db.data.IDatabase;

@Command("exec")
@TxIgnore
public class ExecCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        TransactionState transaction = getTransactionIfExists(request.getSession());
        if (transaction !=  null) {
            ITinyDB server = getTinyDB(request.getServerContext());
            MetaResponse metaResponse = new MetaResponse();
            for (IRequest queuedRequest : transaction) {
                metaResponse.addResponse(executeCommand(server, queuedRequest));
            }
            response.addArray(metaResponse.build());
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
