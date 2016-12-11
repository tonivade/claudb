/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import static tonivade.db.data.DatabaseKey.safeKey;
import static tonivade.db.data.DatabaseValue.string;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;

import tonivade.db.command.ITinyDBCommand;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@Command("decrby")
@ParamLength(2)
@ParamType(DataType.STRING)
public class DecrementByCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        try {
            DatabaseValue value = db.merge(safeKey(request.getParam(0)), string("-" + request.getParam(1)),
                    (oldValue, newValue) -> {
                        int decrement = Integer.parseInt(newValue.getValue().toString());
                        int current = Integer.parseInt(oldValue.getValue().toString());
                        return string(String.valueOf(current + decrement));
                    });
            response.addInt(Integer.parseInt(value.getValue().toString()));
        } catch (NumberFormatException e) {
            response.addError("ERR value is not an integer or out of range");
        }
    }

}
