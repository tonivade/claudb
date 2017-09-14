/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.string;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;

import io.vavr.collection.List;

@ReadOnly
@Command("mget")
@ParamLength(1)
public class MultiGetCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    List<DatabaseValue> result = List.ofAll(request.getParams())
        .map(DatabaseKey::safeKey)
        .filter(key -> db.isType(key, DataType.STRING))
        .map(db::get);
    return convert(result);
  }
}
