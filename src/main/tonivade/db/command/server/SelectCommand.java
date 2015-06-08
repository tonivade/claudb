/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import static java.lang.Integer.parseInt;
import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.IDatabase;

@Command("select")
@ParamLength(1)
public class SelectCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            request.getSession().setCurrentDB(parseInt(request.getParam(0)));
            response.addSimpleStr(RESULT_OK);
        } catch (NumberFormatException e) {
            response.addError("ERR invalid DB index");
        }

    }

}
