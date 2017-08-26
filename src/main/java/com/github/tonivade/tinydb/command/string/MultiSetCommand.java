/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.string;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.data.Database;

@Command("mset")
@ParamLength(2)
public class MultiSetCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    SafeString key = null;
    for (SafeString value : request.getParams()) {
      if (key != null) {
        db.put(safeKey(key), string(value));
        key = null;
      } else {
        key = value;
      }
    }
    return RedisToken.status("OK");
  }

}
