package tonivade.db.command.key;

import static tonivade.db.data.DatabaseKey.safeKey;

import tonivade.db.command.IRedisCommand;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.IDatabase;
import tonivade.server.annotation.Command;
import tonivade.server.annotation.ParamLength;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;

@Command("persist")
@ParamLength(1)
public class PersistCommand implements IRedisCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseKey key = db.overrideKey(safeKey(request.getParam(0)));
        response.addInt(key != null);
    }

}
