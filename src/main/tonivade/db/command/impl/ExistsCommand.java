package tonivade.db.command.impl;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.IDatabase;

public class ExistsCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        if (request.getLength() < 2) {
            response.addError(ERROR);
        } else {
            response.addInt(db.containsKey(request.getParam(1)));
        }
    }

}
