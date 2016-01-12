package tonivade.db.command.key;

import java.util.concurrent.TimeUnit;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.annotation.ParamLength;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;

@Command("ttl")
@ParamLength(1)
public class TimeToLiveCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseKey key = db.getKey(DatabaseKey.safeKey(request.getParam(0)));
        if (key != null) {
            response.addInt(seconds(key));
        } else {
            response.addInt(-2);
        }
    }

    private int seconds(DatabaseKey key) {
        return (int) TimeUnit.MILLISECONDS.toSeconds(key.timeToLive());
    }
}
