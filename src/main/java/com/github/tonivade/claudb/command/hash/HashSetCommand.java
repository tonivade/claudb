/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.hash;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.claudb.command.TinyDBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

@Command("hset")
@ParamLength(3)
@ParamType(DataType.HASH)
public class HashSetCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    DatabaseValue value = hash(entry(request.getParam(1), request.getParam(2)));

    DatabaseValue resultValue = db.merge(safeKey(request.getParam(0)), value,
        (oldValue, newValue) -> {
          Map<SafeString, SafeString> merge = HashMap.empty();
          merge.merge(oldValue.getHash());
          merge.merge(newValue.getHash());
          return hash(merge);
        });

    Map<SafeString, SafeString> resultMap = resultValue.getValue();

    return integer(resultMap.get(request.getParam(1)) == null);
  }
}
