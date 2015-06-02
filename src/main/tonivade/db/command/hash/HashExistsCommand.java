package tonivade.db.command.hash;

import static tonivade.db.data.DatabaseValue.hash;

import java.util.Map;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@Command("hexists")
@ParamLength(2)
@ParamType(DataType.HASH)
public class HashExistsCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue value = db.getOrDefault(request.getParam(0), hash());

        Map<String, String> map = value.getValue();

        response.addInt(map.containsKey(request.getParam(1)));
    }

}
