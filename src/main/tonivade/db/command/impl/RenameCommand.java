package tonivade.db.command.impl;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.IDatabase;

@ParamLength(2)
public class RenameCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        if (db.rename(request.getParam(0), request.getParam(1))) {
            response.addSimpleStr(RESULT_OK);
        } else {
            response.addError("ERR no such key");
        }
    }

}
