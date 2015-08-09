package tonivade.db.command.key;

import java.util.concurrent.TimeUnit;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.IDatabase;

@Command("ttl")
@ParamLength(1)
public class TimeToLiveCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseKey key = db.getKey(DatabaseKey.safeKey(request.getParam(0)));
        if (key != null) {
            response.addInt(TimeUnit.MILLISECONDS.toSeconds(key.timeToLive()));
        } else {
            response.addInt(-2);
        }
    }
}
