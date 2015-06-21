/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static tonivade.db.data.DatabaseValue.list;
import static tonivade.db.redis.SafeString.fromString;

import java.util.LinkedList;
import java.util.List;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.IDatabase;

@Command("lpop")
@ParamLength(1)
@ParamType(DataType.LIST)
public class LeftPopCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<String> removed = new LinkedList<>();
        db.merge(request.getParam(0), list(),
                (oldValue, newValue) -> {
                    List<String> merge = new LinkedList<>();
                    merge.addAll(oldValue.getValue());
                    if (!merge.isEmpty()) {
                        removed.add(merge.remove(0));
                    }
                    return list(merge);
                });

        if (removed.isEmpty()) {
            response.addBulkStr(null);
        } else {
            response.addBulkStr(fromString(removed.remove(0)));
        }
    }

}
