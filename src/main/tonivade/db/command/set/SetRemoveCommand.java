/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.set;

import static java.util.stream.Collectors.toList;
import static tonivade.db.data.DatabaseValue.set;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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

@Command("srem")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetRemoveCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<String> items =  request.getParams().stream().skip(1).collect(toList());
        List<String> removed = new LinkedList<String>();
        db.merge(request.getParam(0), DatabaseValue.EMPTY_SET,
                (oldValue, newValue) -> {
                    Set<String> merge = new HashSet<>();
                    merge.addAll(oldValue.getValue());
                    for (String item : items) {
                        if (merge.remove(item)) {
                            removed.add(item);
                        }
                    }
                    return set(merge);
                });

        response.addInt(removed.size());
    }

}
