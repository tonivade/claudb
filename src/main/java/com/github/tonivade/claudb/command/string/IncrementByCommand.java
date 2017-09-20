/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.string;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.string;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.TinyDBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.claudb.data.Database;

@Command("incrby")
@ParamLength(2)
@ParamType(DataType.STRING)
public class IncrementByCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    try {
      DatabaseValue value = db.merge(safeKey(request.getParam(0)), string(request.getParam(1)),
          (oldValue, newValue) -> {
            int increment = Integer.parseInt(newValue.getValue().toString());
            int current = Integer.parseInt(oldValue.getValue().toString());
            return string(String.valueOf(current + increment));
          });
      return integer(Integer.parseInt(value.getValue().toString()));
    } catch (NumberFormatException e) {
      return error("ERR value is not an integer or out of range");
    }
  }
}
