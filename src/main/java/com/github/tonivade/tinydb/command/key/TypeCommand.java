/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.key;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.tinydb.command.ITinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.IDatabase;

@ReadOnly
@Command("type")
@ParamLength(1)
public class TypeCommand implements ITinyDBCommand {

    @Override
    public void execute(IDatabase db, IRequest request, IResponse response) {
        DatabaseValue value = db.get(safeKey(request.getParam(0)));
        if (value != null) {
            response.addSimpleStr(value.getType().text());
        } else {
            response.addSimpleStr(DataType.NONE.text());
        }
    }

}
