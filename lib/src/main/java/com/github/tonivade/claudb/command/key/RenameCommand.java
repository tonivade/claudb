/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.key;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.responseOk;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.data.Database;

@Command("rename")
@ParamLength(2)
public class RenameCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    if (db.rename(safeKey(request.getParam(0)), safeKey(request.getParam(1)))) {
      return responseOk();
    } else {
      return error("ERR no such key");
    }
  }
}
