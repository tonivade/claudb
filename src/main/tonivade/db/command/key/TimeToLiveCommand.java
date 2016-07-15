package tonivade.db.command.key;

import static tonivade.db.data.DatabaseKey.safeKey;

import java.time.Instant;

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
        DatabaseKey key = db.getKey(safeKey(request.getParam(0)));
        if (key != null) {
            keyExists(response, key);
        } else {
            notExists(response);
        }
    }

    private void keyExists(IResponse response, DatabaseKey key) {
        if (key.expiredAt() != null) {
            hasExpiredAt(response, key);
        } else {
            response.addInt(-1);
        }
    }

    private void hasExpiredAt(IResponse response, DatabaseKey key) {
        Instant now = Instant.now();
        if (!key.isExpired(now)) {
            response.addInt(key.timeToLiveSeconds(now));
        } else {
            notExists(response);
        }
    }

    private void notExists(IResponse response) {
        response.addInt(-2);
    }
}
