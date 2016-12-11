/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.key;

import static tonivade.db.data.DatabaseKey.safeKey;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.data.IDatabase;

@Command("rename")
@ParamLength(2)
public class RenameCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        if (db.rename(safeKey(request.getParam(0)), safeKey(request.getParam(1)))) {
            response.addSimpleStr(IResponse.RESULT_OK);
        } else {
            response.addError("ERR no such key");
        }
    }

}
