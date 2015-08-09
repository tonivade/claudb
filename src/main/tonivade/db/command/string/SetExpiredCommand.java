package tonivade.db.command.string;

import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.string;
import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.IDatabase;
import tonivade.db.redis.SafeString;

@Command("setex")
@ParamLength(3)
public class SetExpiredCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            db.put(safeKey(request.getParam(0), parseTtl(request.getParam(1))), string(request.getParam(2)));
            response.addSimpleStr(RESULT_OK);
        } catch (NumberFormatException e) {
            response.addError("ERR value is not an integer or out of range");
        }
    }

    private int parseTtl(SafeString safeString) throws NumberFormatException {
        return Integer.parseInt(safeString.toString());
    }

}
