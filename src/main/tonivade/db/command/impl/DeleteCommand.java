package tonivade.db.command.impl;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.IDatabase;

public class DeleteCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        db.remove(request.getParam(0));
        response.addSimpleStr(OK);
    }

}
