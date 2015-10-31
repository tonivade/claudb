/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import static tonivade.db.data.DatabaseKey.safeKey;

import tonivade.db.command.IRedisCommand;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.server.annotation.Command;
import tonivade.server.annotation.ParamLength;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;
import tonivade.server.protocol.SafeString;

@Command("del")
@ParamLength(1)
public class DeleteCommand implements IRedisCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        int removed = 0;
        for (SafeString key : request.getParams()) {
            DatabaseValue value = db.remove(safeKey(key));
            if (value != null) {
                removed += 1;
            }
        }
        response.addInt(removed);
    }

}
