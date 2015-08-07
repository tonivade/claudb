/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.zset;

import static java.util.stream.Collectors.toList;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.zset;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
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
import tonivade.db.data.SortedSet;
import tonivade.db.redis.SafeString;


@Command("zrem")
@ParamLength(2)
@ParamType(DataType.ZSET)
public class SortedSetRemoveCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<SafeString> items =  request.getParams().stream().skip(1).collect(toList());
        List<SafeString> removed = new LinkedList<>();
        db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_ZSET,
                (oldValue, newValue) -> {
                    Set<Entry<Double, SafeString>> merge = new SortedSet();
                    merge.addAll(oldValue.getValue());
                    for (SafeString item : items) {
                        if (merge.remove(item)) {
                            removed.add(item);
                        }
                    }
                    return zset(merge);
                });

        response.addInt(removed.size());
    }

}
