/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static java.util.stream.Collectors.toList;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.list;

import java.util.LinkedList;
import java.util.List;

import tonivade.db.command.IRedisCommand;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.annotation.ParamLength;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.protocol.SafeString;

@Command("lpush")
@ParamLength(2)
@ParamType(DataType.LIST)
public class LeftPushCommand implements IRedisCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<SafeString> values = request.getParams().stream().skip(1).collect(toList());

        DatabaseValue result = db.merge(safeKey(request.getParam(0)), list(values),
                (oldValue, newValue) -> {
                    List<SafeString> merge = new LinkedList<>();
                    merge.addAll(newValue.getValue());
                    merge.addAll(oldValue.getValue());
                    return list(merge);
                });

        response.addInt(result.<List<SafeString>>getValue().size());
    }

}
