/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.set;

import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.set;

import java.util.HashSet;
import java.util.Set;

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

@Command("sadd")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetAddCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue value = db.merge(safeKey(request.getParam(0)), set(request.getParam(1)), (oldValue, newValue)-> {
            Set<SafeString> merge = new HashSet<>();
            merge.addAll(oldValue.getValue());
            merge.addAll(newValue.getValue());
            return set(merge);
        });
        response.addInt(value.<Set<String>>getValue().size());
    }

}
