package tonivade.db.command.impl;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.IDatabase;

@ParamLength(1)
public class ExistsCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        response.addInt(db.containsKey(request.getParam(0)));
    }

}
