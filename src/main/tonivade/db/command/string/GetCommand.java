/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import static tonivade.db.data.DatabaseKey.safeKey;

import tonivade.db.command.IRedisCommand;
import tonivade.db.command.RedisResponse;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.DataType;
import tonivade.db.data.IDatabase;
import tonivade.server.annotation.Command;
import tonivade.server.annotation.ParamLength;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;

@ReadOnly
@Command("get")
@ParamLength(1)
@ParamType(DataType.STRING)
public class GetCommand implements IRedisCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        new RedisResponse(response).addValue(db.get(safeKey(request.getParam(0))));
    }

}
