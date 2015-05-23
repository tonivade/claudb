package tonivade.db.command.impl;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@ParamLength(1)
public class DeleteCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        int removed = 0;
        for (String key : request.getParams()) {
            DatabaseValue value = db.remove(key);
            if (value != null) {
                removed += 1;
            }
        }
        response.addInt(removed);
    }

}
