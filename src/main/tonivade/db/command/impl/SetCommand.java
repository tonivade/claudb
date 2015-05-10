package tonivade.db.command.impl;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

/**
 *
 * @author tomby
 *
 */
public class SetCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue value = new DatabaseValue();
        value.setType(DataType.STRING);
        value.setValue(request.getParam(1));
        db.merge(request.getParam(0), value, (oldValue, newValue) -> newValue);
        response.addSimpleStr(OK);
    }

}
