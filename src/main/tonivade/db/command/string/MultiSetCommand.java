/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

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

@Command("mset")
@ParamLength(2)
public class MultiSetCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        SafeString key = null;
        for (SafeString value : request.getParams()) {
            if (key != null) {
                db.put(safeKey(key), string(value));
                key = null;
            } else {
                key = value;
            }
        }
        response.addSimpleStr("OK");
    }

}
