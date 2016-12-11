/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.server;

import static java.lang.Integer.parseInt;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.ReadOnly;
import tonivade.db.data.IDatabase;

@ReadOnly
@Command("select")
@ParamLength(1)
public class SelectCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            getSessionState(request.getSession()).setCurrentDB(parseCurrentDB(request));
            response.addSimpleStr(IResponse.RESULT_OK);
        } catch (NumberFormatException e) {
            response.addError("ERR invalid DB index");
        }

    }

    private int parseCurrentDB(IRequest request) {
        return parseInt(request.getParam(0).toString());
    }

}
