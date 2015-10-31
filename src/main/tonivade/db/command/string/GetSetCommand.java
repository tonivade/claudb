/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.string;

import tonivade.db.command.IRedisCommand;
import tonivade.db.command.RedisResponse;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.server.annotation.Command;
import tonivade.server.annotation.ParamLength;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;

@Command("getset")
@ParamLength(2)
@ParamType(DataType.STRING)
public class GetSetCommand implements IRedisCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue value = db.put(safeKey(request.getParam(0)), string(request.getParam(1)));
        new RedisResponse(response).addValue(value);
    }

}
