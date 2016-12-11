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
import com.github.tonivade.resp.protocol.SafeString;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@Command("del")
@ParamLength(1)
public class DeleteCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        int removed = 0;
        for (SafeString key : request.getParams()) {
            DatabaseValue value = db.remove(safeKey(key));
            if (value != null) {
                removed += 1;
            }
        }
        response.addInt(removed);
    }

}
