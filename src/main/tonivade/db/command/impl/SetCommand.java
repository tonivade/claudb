package tonivade.db.command.impl;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.DataType;
import tonivade.db.data.Database;
import tonivade.db.data.DatabaseValue;

/**
 *
 * @author tomby
 *
 */
public class SetCommand implements ICommand {

    @Override
    public void execute(Database db, IRequest request, IResponse response) {
        if (request.getLength() < 3) {
            response.addError(ERROR);
        } else {
            DatabaseValue value = new DatabaseValue();
            value.setType(DataType.STRING);
            value.setValue(request.getParam(2));
            db.put(request.getParam(1), value);
            response.addSimpleStr(OK);
        }
    }

}
