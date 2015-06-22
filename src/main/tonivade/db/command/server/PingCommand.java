/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import static tonivade.db.redis.SafeString.safeString;
import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.PubSubAllowed;
import tonivade.db.data.IDatabase;

@Command("ping")
@PubSubAllowed
public class PingCommand implements ICommand {

    public static final String PONG = "PONG";

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        if (request.getLength() > 0) {
            response.addBulkStr(safeString(request.getParam(0)));
        } else {
            response.addSimpleStr(PONG);
        }
    }

}
