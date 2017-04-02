/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.set;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;

import java.util.HashSet;
import java.util.Set;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.Database;

@Command("sadd")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetAddCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, IRequest request) {
    DatabaseValue value = db.merge(safeKey(request.getParam(0)), set(request.getParam(1)), (oldValue, newValue)-> {
      Set<SafeString> merge = new HashSet<>();
      merge.addAll(oldValue.getValue());
      merge.addAll(newValue.getValue());
      return set(merge);
    });
    return RedisToken.integer(value.<Set<String>>getValue().size());
  }

}
