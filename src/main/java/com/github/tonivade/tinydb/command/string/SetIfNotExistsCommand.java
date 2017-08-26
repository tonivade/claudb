/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.string;

import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;

@Command("setnx")
@ParamLength(2)
public class SetIfNotExistsCommand implements TinyDBCommand {
  
  @Override
  public RedisToken execute(Database db, Request request) {
    DatabaseKey key = safeKey(request.getParam(0));
    DatabaseValue value = string(request.getParam(1));
    return integer(putValueIfNotExists(db, key, value).equals(value));
  }

  private DatabaseValue putValueIfNotExists(Database db, DatabaseKey key, DatabaseValue value) {
    return db.merge(key, value, (oldValue, newValue) -> oldValue);
  }
}
