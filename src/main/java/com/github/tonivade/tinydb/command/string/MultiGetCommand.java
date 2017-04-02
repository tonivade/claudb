/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.string;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.command.ITinyDBCommand;

import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.data.IDatabase;

@ReadOnly
@Command("mget")
@ParamLength(1)
public class MultiGetCommand implements ITinyDBCommand {

  @Override
  public RedisToken execute(IDatabase db, IRequest request) {
    List<DatabaseValue> result = new ArrayList<>(request.getLength());
    for (DatabaseKey key : request.getParams().stream().map((item) -> DatabaseKey.safeKey(item)).collect(Collectors.toList())) {
      result.add(db.get(key));
    }
    return convert(result);
  }
}
