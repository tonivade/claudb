package com.github.tonivade.tinydb.command.key;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.tinydb.command.ITinyDBCommand;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.IDatabase;

@Command("persist")
@ParamLength(1)
public class PersistCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseKey key = db.overrideKey(safeKey(request.getParam(0)));
        response.addInt(key != null);
    }

}
