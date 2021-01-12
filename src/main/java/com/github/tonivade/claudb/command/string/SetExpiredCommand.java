/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.string;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.responseOk;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.string;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.data.Database;

@Command("setex")
@ParamLength(3)
public class SetExpiredCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    try {
      db.put(safeKey(request.getParam(0)), string(request.getParam(2))
               .expiredAt(parseTtl(request.getParam(1))));
      return responseOk();
    } catch (NumberFormatException e) {
      return error("ERR value is not an integer or out of range");
    }
  }

  private int parseTtl(SafeString safeString) {
    return Integer.parseInt(safeString.toString());
  }
}
