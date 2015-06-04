/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.hash;

import static tonivade.db.data.DatabaseValue.hash;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.IDatabase;

@Command("hdel")
@ParamLength(2)
@ParamType(DataType.HASH)
public class HashDeleteCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<String> keys = request.getParams().stream().skip(1).collect(Collectors.toList());

        List<String> removedKeys = new LinkedList<>();
        db.merge(request.getParam(0), hash(), (oldValue, newValue) -> {
            Map<String, String> merge = new HashMap<>();
            merge.putAll(oldValue.getValue());
            for (String key : keys) {
                String data = merge.remove(key);
                if (data != null) {
                    removedKeys.add(data);
                }
            }
            return hash(merge.entrySet());
        });

        response.addInt(!removedKeys.isEmpty());
    }

}
