/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.list;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.list;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

@Command("lpush")
@ParamLength(2)
@ParamType(DataType.LIST)
public class LeftPushCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    ImmutableList<SafeString> values = request.getParams().asList().tail().reverse();

    DatabaseValue result = db.merge(safeKey(request.getParam(0)), list(values),
        (oldValue, newValue) -> list(newValue.getList().appendAll(oldValue.getList())));

    return RedisToken.integer(result.size());
  }
}
