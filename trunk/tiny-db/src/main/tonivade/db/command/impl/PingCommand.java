package tonivade.db.command.impl;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.IDatabase;

public class PingCommand implements ICommand {

    public static final String PONG = "PONG";

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        if (request.getLength() > 1) {
            response.addBulkStr(request.getParam(1));
        } else {
            response.addSimpleStr(PONG);
        }
    }

}
