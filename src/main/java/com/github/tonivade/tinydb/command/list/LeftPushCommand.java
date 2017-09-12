/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.list;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.list;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseValue;

import io.vavr.collection.List;
import io.vavr.collection.Stream;

@Command("lpush")
@ParamLength(2)
@ParamType(DataType.LIST)
public class LeftPushCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    List<SafeString> values = Stream.ofAll(request.getParams()).tail().toList();

    DatabaseValue result = db.merge(safeKey(request.getParam(0)), list(values),
        (oldValue, newValue) -> {
          List<SafeString> oldList = oldValue.getValue();
          List<SafeString> newList = newValue.getValue();
          return list(newList.appendAll(oldList));
        });

    return RedisToken.integer(result.<List<SafeString>>getValue().size());
  }

}
