/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@Command("mget")
@ParamLength(2)
public class MultiSetCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        String key = null;
        for (String value : request.getParams()) {
            if (key != null) {
                db.merge(key, DatabaseValue.string(value), (oldValue, newValue) -> newValue);
                key = null;
            } else {
                key = value;
            }
        }
        response.addSimpleStr("OK");
    }

}
