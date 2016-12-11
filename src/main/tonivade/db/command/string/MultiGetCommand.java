/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.TinyDBResponse;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@ReadOnly
@Command("mget")
@ParamLength(1)
public class MultiGetCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        List<DatabaseValue> result = new ArrayList<>(request.getLength());
        for (DatabaseKey key : request.getParams().stream().map((item) -> DatabaseKey.safeKey(item)).collect(Collectors.toList())) {
            result.add(db.get(key));
        }
        new TinyDBResponse(response).addArrayValue(result);
    }

}
