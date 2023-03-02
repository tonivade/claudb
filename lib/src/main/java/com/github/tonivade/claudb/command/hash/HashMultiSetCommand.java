/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.hash;

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

import java.util.HashMap;
import java.util.Map;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.resp.protocol.RedisToken.responseOk;

@Command("hmset")
@ParamLength(3)
@ParamType(DataType.HASH)
public class HashMultiSetCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {

    for (int paramNumber = 1; paramNumber < request.getLength(); paramNumber += 2) {

      SafeString mapKey = request.getParam(paramNumber);
      SafeString mapVal = request.getParam(paramNumber + 1);

      DatabaseValue value = hash(entry(mapKey, mapVal));

      db.merge(safeKey(request.getParam(0)), value,
          (oldValue, newValue) -> {
            Map<SafeString, SafeString> merge = new HashMap<>();
            merge.putAll(oldValue.getHash());
            merge.putAll(newValue.getHash());
            return hash(merge);
          }
      );

    }

    return responseOk();
  }
}
