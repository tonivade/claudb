/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.hash;

import static java.util.stream.Collectors.toList;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.hash;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tonivade.db.command.IRedisCommand;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.server.annotation.Command;
import tonivade.server.annotation.ParamLength;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;
import tonivade.server.protocol.SafeString;

@Command("hdel")
@ParamLength(2)
@ParamType(DataType.HASH)
public class HashDeleteCommand implements IRedisCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<SafeString> keys = request.getParams().stream().skip(1).collect(toList());

        List<SafeString> removedKeys = new LinkedList<>();
        db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_HASH, (oldValue, newValue) -> {
            Map<SafeString, SafeString> merge = new HashMap<>();
            merge.putAll(oldValue.getValue());
            for (SafeString key : keys) {
                SafeString data = merge.remove(key);
                if (data != null) {
                    removedKeys.add(data);
                }
            }
            return hash(merge.entrySet());
        });

        response.addInt(!removedKeys.isEmpty());
    }

}
