package tonivade.db.command.string;

import static tonivade.db.data.DatabaseKey.ttlKey;
import static tonivade.db.data.DatabaseValue.string;

import java.util.concurrent.TimeUnit;

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
        db.put(ttlKey(request.getParam(0), parseTtl(request.getParam(1))), string(request.getParam(2)));
        response.addSimpleStr(RESULT_OK);
    }

    private long parseTtl(SafeString safeString) {
        return TimeUnit.SECONDS.toMillis(Integer.parseInt(safeString.toString()));
    }

}
