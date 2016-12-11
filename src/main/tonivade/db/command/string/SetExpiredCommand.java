package tonivade.db.command.string;

import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.string;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.protocol.SafeString;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.data.IDatabase;

@Command("setex")
@ParamLength(3)
public class SetExpiredCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            db.put(safeKey(request.getParam(0), parseTtl(request.getParam(1))), string(request.getParam(2)));
            response.addSimpleStr(IResponse.RESULT_OK);
        } catch (NumberFormatException e) {
            response.addError("ERR value is not an integer or out of range");
        }
    }

    private int parseTtl(SafeString safeString) throws NumberFormatException {
        return Integer.parseInt(safeString.toString());
    }

}
