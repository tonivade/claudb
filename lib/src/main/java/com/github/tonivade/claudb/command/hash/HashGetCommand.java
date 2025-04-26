/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.hash;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.Map;

@ReadOnly
@Command("hget")
@ParamLength(2)
@ParamType(DataType.HASH)
public class HashGetCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    Map<SafeString, SafeString> map = db.getHash(request.getParam(0));
    SafeString key = request.getParam(1);
    if (map.containsKey(key)) {
      return RedisToken.string(map.get(key));
    }
    return RedisToken.nullString();
  }
}
