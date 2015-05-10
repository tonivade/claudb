package tonivade.db.command.impl;

import java.util.LinkedList;
import java.util.List;
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
        if (value != null) {
            if (value.getType() == DataType.HASH) {
                List<String> result = new LinkedList<>();
                Map<String, String> map = value.getValue();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    result.add(entry.getKey());
                    result.add(entry.getValue());
                }
                response.addArray(result);
            } else {
                response.addError("WRONGTYPE Operation against a key holding the wrong kind of value");
            }
        } else {
            response.addArray(null);
        }
    }

}
