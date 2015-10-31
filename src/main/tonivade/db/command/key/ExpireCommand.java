package tonivade.db.command.key;

import static tonivade.db.data.DatabaseKey.safeKey;

import tonivade.db.command.IRedisCommand;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.IDatabase;
import tonivade.server.annotation.Command;
import tonivade.server.annotation.ParamLength;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;
import tonivade.server.protocol.SafeString;

@Command("expire")
@ParamLength(2)
public class ExpireCommand implements IRedisCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            DatabaseKey key = db.overrideKey(safeKey(request.getParam(0), parsetTtl(request.getParam(1))));
            response.addInt(key != null);
        } catch (NumberFormatException e) {
            response.addError("ERR value is not an integer or out of range");
        }
    }

    private int parsetTtl(SafeString param) throws NumberFormatException {
        return Integer.parseInt(param.toString());
    }

}
