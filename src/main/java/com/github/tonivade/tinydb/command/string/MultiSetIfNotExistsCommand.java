/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.string;

import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.entry;
import static com.github.tonivade.tinydb.data.DatabaseValue.string;

import java.util.HashSet;
import java.util.Set;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;

import io.vavr.Tuple2;

@Command("msetnx")
@ParamLength(2)
public class MultiSetIfNotExistsCommand implements TinyDBCommand {
  @Override
  public RedisToken execute(Database db, Request request) {
    Set<Tuple2<SafeString, SafeString>> pairs = toPairs(request);
    if (noneExists(db, pairs)) {
      pairs.forEach(entry -> db.put(safeKey(entry._1()), string(entry._2())));
      return RedisToken.integer(1);
    }
    return RedisToken.integer(0);
  }

  private boolean noneExists(Database db, Set<Tuple2<SafeString, SafeString>> pairs) {
    return pairs.stream()
        .map(Tuple2::_1)
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
