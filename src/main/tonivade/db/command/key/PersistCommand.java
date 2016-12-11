package tonivade.db.command.key;

import static tonivade.db.data.DatabaseKey.safeKey;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.IDatabase;

@Command("persist")
@ParamLength(1)
public class PersistCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseKey key = db.overrideKey(safeKey(request.getParam(0)));
        response.addInt(key != null);
    }

}
