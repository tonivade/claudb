package tonivade.db.command.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@ParamLength(1)
@ParamType(DataType.HASH)
public class HashGetAllCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue value = db.get(request.getParam(0));
        if (value != null) {
            List<String> result = new LinkedList<>();
            Map<String, String> map = value.getValue();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                result.add(entry.getKey());
                result.add(entry.getValue());
            }
            response.addArray(result);
        } else {
            response.addArray(null);
        }
    }

}
