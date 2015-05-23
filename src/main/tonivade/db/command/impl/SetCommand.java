package tonivade.db.command.impl;

import static tonivade.db.data.DatabaseValue.string;
import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.IDatabase;

/**
 *
 * @author tomby
 *
 */
@ParamLength(2)
public class SetCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        db.put(request.getParam(0), string(request.getParam(1)));
        response.addSimpleStr(OK);
    }

}
