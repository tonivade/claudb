package tonivade.db.command.transaction;

import tonivade.db.TransactionState;
import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.TxIgnore;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.command.ISession;

@Command("multi")
@TxIgnore
public class MultiCommand implements ITinyDBCommand {

    private static final String TRASACTION_KEY = "tx";

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        if (!isTxActive(request.getSession())) {
            createTransaction(request.getSession());
            response.addSimpleStr(IResponse.RESULT_OK);
        } else {
            response.addError("ERR MULTI calls can not be nested");
        }
    }

    private void createTransaction(ISession session) {
        session.putValue(TRASACTION_KEY, new TransactionState());
    }

    private boolean isTxActive(ISession session) {
        return session.getValue(TRASACTION_KEY) != null;
    }

}
