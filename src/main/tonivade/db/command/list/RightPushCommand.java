/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.list;

import static java.util.stream.Collectors.toList;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.list;

import java.util.LinkedList;
import java.util.List;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.protocol.SafeString;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@Command("rpush")
@ParamLength(2)
@ParamType(DataType.LIST)
public class RightPushCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<SafeString> values = request.getParams().stream().skip(1).collect(toList());

        DatabaseValue result = db.merge(safeKey(request.getParam(0)), list(values),
                (oldValue, newValue) -> {
                    List<SafeString> merge = new LinkedList<>();
                    merge.addAll(oldValue.getValue());
                    merge.addAll(newValue.getValue());
                    return list(merge);
                });

        response.addInt(result.<List<SafeString>>getValue().size());
    }

}
