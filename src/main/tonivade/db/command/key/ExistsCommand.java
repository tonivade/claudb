/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import static tonivade.db.data.DatabaseKey.safeKey;

import tonivade.db.command.IRedisCommand;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.IDatabase;
import tonivade.server.annotation.Command;
import tonivade.server.annotation.ParamLength;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;

@ReadOnly
@Command("exists")
@ParamLength(1)
public class ExistsCommand implements IRedisCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        response.addInt(db.containsKey(safeKey(request.getParam(0))));
    }

}
