/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command.string;

import static tonivade.db.data.DatabaseValue.string;
import tonivade.db.command.ICommand;
import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.command.annotation.Command;
import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@Command("append")
@ParamLength(1)
@ParamType(DataType.STRING)
public class AppendCommand implements ICommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue value = db.merge(
                request.getParam(0), string(request.getParam(1)), (oldValue, newValue) -> {
                    if (oldValue != null) {
                        oldValue.append(newValue.getValue());
                        return oldValue;
                    }
                    return newValue;
                });

        response.addInt(value.<String>getValue().length());
    }

}
