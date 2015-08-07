/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.list;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.db.redis.SafeString;

@Command("lpush")
@ParamLength(2)
@ParamType(DataType.LIST)
public class LeftPushCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<String> values = request.getParams().stream().skip(1).map(SafeString::toString).collect(Collectors.toList());

        DatabaseValue result = db.merge(safeKey(request.getParam(0)), list(values),
                (oldValue, newValue) -> {
                    List<String> merge = new LinkedList<>();
                    merge.addAll(newValue.getValue());
                    merge.addAll(oldValue.getValue());
                    return list(merge);
                });

        response.addInt(result.<List<String>>getValue().size());
    }

}
