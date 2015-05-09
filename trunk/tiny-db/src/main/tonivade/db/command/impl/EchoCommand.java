package tonivade.db.command.impl;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.IDatabase;

public class EchoCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        if (request.getLength() < 2) {
            response.addError(ERROR);
        } else {
            response.addBulkStr(request.getParam(1));
        }
    }

}
