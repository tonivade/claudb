package tonivade.db.command.key;

import static tonivade.db.data.DatabaseKey.ttlKey;
import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.IDatabase;
import tonivade.db.redis.SafeString;

@Command("expire")
@ParamLength(2)
public class ExpireCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            DatabaseKey key = db.overrideKey(ttlKey(request.getParam(0), parsetTtl(request.getParam(1))));
            response.addInt(key != null);
        } catch (NumberFormatException e) {
            response.addError("ERR value is not an integer or out of range");
        }
    }

    private int parsetTtl(SafeString param) throws NumberFormatException {
        return Integer.parseInt(param.toString());
    }

}
