/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.key;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;

@Command("expire")
@ParamLength(2)
public class ExpireCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    try {
      DatabaseValue value = db.get(safeKey(request.getParam(0)));
      if (value != null) {
        db.put(safeKey(request.getParam(0)), value.expiredAt(parsetTtl(request.getParam(1))));
      }
      return integer(value != null);
    } catch (NumberFormatException e) {
      return error("ERR value is not an integer or out of range");
    }
  }

  private int parsetTtl(SafeString param) {
    return Integer.parseInt(param.toString());
  }
}
