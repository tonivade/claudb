/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.set;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.set;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

@Command("sadd")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetAddCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    List<SafeString> values = request.getParams().stream().skip(1).collect(toList());
    DatabaseValue value = db.merge(safeKey(request.getParam(0)), set(values),
      (oldValue, newValue) -> set(oldValue.getSet().appendAll(newValue.getSet())));
    return integer(value.size());
  }
}
