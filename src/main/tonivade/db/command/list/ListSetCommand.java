/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static tonivade.db.data.DatabaseKey.safeKey;

import java.util.ArrayList;
import java.util.List;

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

@Command("lset")
@ParamLength(3)
@ParamType(DataType.LIST)
public class ListSetCommand implements IRedisCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            int index = Integer.parseInt(request.getParam(1).toString());
            db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_LIST,
                    (oldValue, newValue) -> {
                        List<SafeString> merge = new ArrayList<>(oldValue.<List<SafeString>>getValue());
                        merge.set(index > -1 ? index : merge.size() + index, request.getParam(2));
                        return DatabaseValue.list(merge);
                    });
            response.addSimpleStr("OK");
        } catch (NumberFormatException e) {
            response.addError("ERR value is not an integer or out of range");
        } catch (IndexOutOfBoundsException e) {
            response.addError("ERR index out of range");
        }
    }

}
