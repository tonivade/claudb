/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.string;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.Database;

@Command("decrby")
@ParamLength(2)
@ParamType(DataType.STRING)
public class DecrementByCommand implements TinyDBCommand {

  @Override
  public RedisToken<?> execute(Database db, IRequest request) {
    try {
      DatabaseValue value = db.merge(safeKey(request.getParam(0)), string("-" + request.getParam(1)),
          (oldValue, newValue) -> {
            int decrement = Integer.parseInt(newValue.getValue().toString());
            int current = Integer.parseInt(oldValue.getValue().toString());
            return string(String.valueOf(current + decrement));
          });
      return RedisToken.integer(Integer.parseInt(value.getValue().toString()));
    } catch (NumberFormatException e) {
      return RedisToken.error("ERR value is not an integer or out of range");
    }
  }

}
