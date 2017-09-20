/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.list;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;

import io.vavr.collection.List;

@ReadOnly
@Command("lrange")
@ParamLength(3)
@ParamType(DataType.LIST)
public class ListRangeCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    try {
      DatabaseValue value = db.getOrDefault(safeKey(request.getParam(0)), DatabaseValue.EMPTY_LIST);
      List<SafeString> list = value.getValue();

      int from = Integer.parseInt(request.getParam(1).toString());
      if (from < 0) {
        from = list.size() + from;
      }
      int to = Integer.parseInt(request.getParam(2).toString());
      if (to < 0) {
        to = list.size() + to;
      }

      int min = Math.min(from, to);
      int max = Math.max(from, to);

      // TODO: use Array
      List<SafeString> result = list.toJavaStream().skip(min).limit((max - min) + 1).collect(List.collector());

      return convert(result);
    } catch (NumberFormatException e) {
      return error("ERR value is not an integer or out of range");
    }
  }
}
