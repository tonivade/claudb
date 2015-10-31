/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.string;

import tonivade.db.command.IRedisCommand;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.server.annotation.Command;
import tonivade.server.annotation.ParamLength;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;

@Command("decr")
@ParamLength(1)
@ParamType(DataType.STRING)
public class DecrementCommand implements IRedisCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            DatabaseValue value = db.merge(safeKey(request.getParam(0)), string("-1"),
                    (oldValue, newValue) -> {
                        int current = Integer.parseInt(oldValue.getValue().toString());
                        return string(String.valueOf(current - 1));
                    });
            response.addInt(value.getValue());
        } catch (NumberFormatException e) {
            response.addError("ERR value is not an integer or out of range");
        }
    }

}
