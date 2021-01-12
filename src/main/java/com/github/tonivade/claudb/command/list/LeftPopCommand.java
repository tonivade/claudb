/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.list;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.list;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.string;

import java.util.LinkedList;
import java.util.List;

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

@Command("lpop")
@ParamLength(1)
@ParamType(DataType.LIST)
public class LeftPopCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    List<SafeString> removed = new LinkedList<>();
    db.merge(safeKey(request.getParam(0)), DatabaseValue.EMPTY_LIST,
        (oldValue, newValue) -> {
          ImmutableList<SafeString> list = oldValue.getList();
          list.head().stream().forEach(removed::add);
          return list(list.tail());
        });

    if (removed.isEmpty()) {
      return nullString();
    } else {
      return string(removed.remove(0));
    }
  }
}
