/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import tonivade.db.command.IRedisCommand;
import tonivade.db.data.IDatabase;
import tonivade.server.annotation.Command;
import tonivade.server.command.IRequest;
import tonivade.server.command.IResponse;

@Command("flushdb")
public class FlushDBCommand implements IRedisCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        db.clear();
        response.addSimpleStr(IResponse.RESULT_OK);
    }

}
