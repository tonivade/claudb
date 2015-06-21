/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import static tonivade.db.redis.SafeString.fromString;
import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.IDatabase;

@Command("echo")
@ParamLength(1)
public class EchoCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        response.addBulkStr(fromString(request.getParam(0)));
    }

}
