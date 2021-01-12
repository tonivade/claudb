/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.key;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.claudb.data.Database;

@Command("del")
@ParamLength(1)
public class DeleteCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    int removed = 0;
    for (SafeString key : request.getParams()) {
      DatabaseValue value = db.remove(safeKey(key));
      if (value != null) {
        removed += 1;
      }
    }
    return integer(removed);
  }
}
