/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.string;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.SafeString.append;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.string;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;

@Command("append")
@ParamLength(1)
@ParamType(DataType.STRING)
public class AppendCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    DatabaseValue value = db.merge(safeKey(request.getParam(0)), string(request.getParam(1)),
        (oldValue, newValue) -> {
          return string(append(oldValue.getString(), newValue.getString()));
        });
    return integer(value.getString().length());
  }

}
