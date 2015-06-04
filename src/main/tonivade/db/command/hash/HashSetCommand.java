/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.hash;

import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import java.util.Map;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@Command("hset")
@ParamLength(3)
@ParamType(DataType.HASH)
public class HashSetCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue value = hash(entry(request.getParam(1), request.getParam(2)));

        DatabaseValue resultValue = db.merge(request.getParam(0), value,
                (oldValue, newValue) -> {
                    DatabaseValue merged = hash();
                    Map<String, String> hash = merged.getValue();
                    hash.putAll(oldValue.getValue());
                    hash.putAll(newValue.getValue());
                    return merged;
                });

        Map<String, String> resultMap = resultValue.getValue();

        response.addInt(resultMap.get(request.getParam(1)) == null);
    }

}
