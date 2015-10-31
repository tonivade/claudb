/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import tonivade.db.command.IRedisCommand;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.Command;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;

@Command("flushdb")
public class FlushDBCommand implements IRedisCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        db.clear();
        response.addSimpleStr(IResponse.RESULT_OK);
    }

}
