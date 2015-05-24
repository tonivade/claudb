package tonivade.db.command.impl;

import static tonivade.db.data.DatabaseValue.hash;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@ParamLength(2)
@ParamType(DataType.HASH)
public class HashDeleteCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<String> keys = request.getParams().stream().skip(1).collect(Collectors.toList());

        DatabaseValue value = db.getOrDefault(request.getParam(0), hash());

        List<String> removedKeys = new LinkedList<>();
        Map<String, String> map = value.getValue();
        for (String key : keys) {
            String data = map.remove(key);
            if (data != null) {
                removedKeys.add(data);
            }
        }

        response.addInt(!removedKeys.isEmpty());
    }

}
