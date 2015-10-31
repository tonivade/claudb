/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tonivade.db.command.IRedisCommand;
import tonivade.db.command.RedisResponse;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.annotation.ParamLength;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;

@ReadOnly
@Command("mget")
@ParamLength(1)
public class MultiGetCommand implements IRedisCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<DatabaseValue> result = new ArrayList<>(request.getLength());
        for (DatabaseKey key : request.getParams().stream().map((item) -> DatabaseKey.safeKey(item)).collect(Collectors.toList())) {
            result.add(db.get(key));
        }
        new RedisResponse(response).addArrayValue(result);
    }

}
