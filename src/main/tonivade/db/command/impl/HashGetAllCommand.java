package tonivade.db.command.impl;

import java.util.Map;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

public class HashGetAllCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue value = db.get(request.getParam(0));
        if (value.getType() == DataType.HASH) {
            Map<String, String> map = value.getValue();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                response.addBulkStr(entry.getKey());
                response.addBulkStr(entry.getValue());
            }
        } else {
            response.addError("WRONGTYPE Operation against a key holding the wrong kind of value");
        }
    }

}
