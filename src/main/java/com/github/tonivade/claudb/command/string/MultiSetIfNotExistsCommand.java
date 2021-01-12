/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.string;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.resp.protocol.RedisToken.integer;

import java.util.HashSet;
import java.util.Set;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

@Command("msetnx")
@ParamLength(2)
public class MultiSetIfNotExistsCommand implements DBCommand {
  @Override
  public RedisToken execute(Database db, Request request) {
    Set<Tuple2<SafeString, SafeString>> pairs = toPairs(request);
    if (noneExists(db, pairs)) {
      pairs.forEach(entry -> db.put(safeKey(entry.get1()), string(entry.get2())));
      return integer(1);
    }
    return integer(0);
  }

  private boolean noneExists(Database db, Set<Tuple2<SafeString, SafeString>> pairs) {
    return pairs.stream()
        .map(Tuple2::get1)
        .map(DatabaseKey::safeKey)
        .noneMatch(db::containsKey);
  }

  private Set<Tuple2<SafeString, SafeString>> toPairs(Request request) {
    Set<Tuple2<SafeString, SafeString>> pairs = new HashSet<>();
    SafeString key = null;
    for (SafeString value : request.getParams()) {
      if (key != null) {
        pairs.add(entry(key, value));
        key = null;
      } else {
        key = value;
      }
    }
    return pairs;
  }
}
