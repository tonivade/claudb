/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.hash;

import static java.util.stream.Collectors.toList;
import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.hash;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

@Command("hdel")
@ParamLength(2)
@ParamType(DataType.HASH)
public class HashDeleteCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<SafeString> keys = request.getParams().stream().skip(1).collect(toList());

        List<SafeString> removedKeys = new LinkedList<>();
        db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_HASH, (oldValue, newValue) -> {
            Map<SafeString, SafeString> merge = new HashMap<>();
            merge.putAll(oldValue.getValue());
            for (SafeString key : keys) {
                SafeString data = merge.remove(key);
                if (data != null) {
                    removedKeys.add(data);
                }
            }
            return hash(merge.entrySet());
        });

        response.addInt(!removedKeys.isEmpty());
    }

}
