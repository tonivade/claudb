/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.set;

import static java.util.stream.Collectors.toList;
import static tonivade.db.data.DatabaseKey.safeKey;

import java.util.HashSet;
import java.util.Set;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.annotation.ParamLength;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.protocol.SafeString;

@ReadOnly
@Command("sinter")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetIntersectionCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue first = db.getOrDefault(safeKey(request.getParam(0)), DatabaseValue.EMPTY_SET);
        Set<SafeString> result = new HashSet<>(first.<Set<SafeString>>getValue());
        for (DatabaseKey param : request.getParams().stream().skip(1).map((item) -> safeKey(item)).collect(toList())) {
            result.retainAll(db.getOrDefault(param, DatabaseValue.EMPTY_SET).<Set<SafeString>>getValue());
        }
        response.addArray(result);
    }

}
