package tonivade.db.command.key;

import static tonivade.db.data.DatabaseKey.safeKey;
import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.IDatabase;

@Command("persist")
@ParamLength(1)
public class PersistCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseKey key = db.overrideKey(safeKey(request.getParam(0)));
        response.addInt(key != null);
    }

}
