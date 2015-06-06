/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.set;

import static tonivade.db.data.DatabaseValue.set;

import java.util.HashSet;
import java.util.Set;
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

@Command("sunion")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetUnionCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue first = db.getOrDefault(request.getParam(0), set());
        Set<String> result = new HashSet<>(first.<Set<String>>getValue());
        for (String param : request.getParams().stream().skip(1).collect(Collectors.toList())) {
            result.addAll(db.getOrDefault(param, set()).<Set<String>>getValue());
        }
        response.addArray(result);
    }

}
