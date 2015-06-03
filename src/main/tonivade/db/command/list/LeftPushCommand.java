/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static tonivade.db.data.DatabaseValue.list;

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

@Command("lpush")
@ParamLength(2)
@ParamType(DataType.LIST)
public class LeftPushCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<String> values = request.getParams().stream().skip(1).collect(Collectors.toList());

        DatabaseValue result = db.merge(request.getParam(0), list(values), (oldValue, newValue) -> {
           if (oldValue != null) {
               List<String> oldList = oldValue.getValue();
               List<String> newList = newValue.getValue();
               oldList.addAll(0, newList);
               return oldValue;
           }
           return newValue;
        });

        response.addInt(result.<List<String>>getValue().size());
    }

}
