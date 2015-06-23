/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static java.util.stream.Collectors.toList;

import java.util.List;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@Command("lrange")
@ParamLength(3)
@ParamType(DataType.LIST)
public class ListRangeCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            DatabaseValue value = db.getOrDefault(request.getParam(0), DatabaseValue.EMPTY_LIST);
            List<String> list = value.getValue();

            int from = Integer.parseInt(request.getParam(1));
            if (from < 0) {
                from = list.size() + from;
            }
            int to = Integer.parseInt(request.getParam(2));
            if (to < 0) {
                to = list.size() + to;
            }

            int min = Math.min(from, to);
            int max = Math.max(from, to);

            List<String> result = list.stream().skip(min).limit((max - min) + 1).collect(toList());

            response.addArray(result);
        } catch (NumberFormatException e) {
            response.addError("ERR value is not an integer or out of range");
        }
    }

}
