/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.set;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;

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

import io.vavr.collection.Set;

@Command("sadd")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetAddCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    DatabaseValue value = db.merge(safeKey(request.getParam(0)), set(request.getParam(1)), 
      (oldValue, newValue) -> {
        Set<SafeString> oldSet = oldValue.getValue();
        Set<SafeString> newSet = newValue.getValue();
        return set(oldSet.addAll(newSet));
      });
    return integer(value.size());
  }

}
