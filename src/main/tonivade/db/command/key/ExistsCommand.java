/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import static tonivade.db.data.DatabaseKey.safeKey;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.annotation.ParamLength;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;

@ReadOnly
@Command("exists")
@ParamLength(1)
public class ExistsCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseKey key = db.getKey(safeKey(request.getParam(0)));
        response.addInt(key != null ? !key.isExpired() : false);
    }

}
