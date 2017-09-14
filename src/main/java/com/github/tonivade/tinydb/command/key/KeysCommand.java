/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.key;

import java.time.Instant;
import java.util.function.Predicate;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.glob.GlobPattern;

import io.vavr.Tuple2;
import io.vavr.collection.Set;

@ReadOnly
@Command("keys")
@ParamLength(1)
public class KeysCommand implements TinyDBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    GlobPattern pattern = createPattern(request.getParam(0));
    Set<SafeString> keys = db.entrySet()
        .filter(matchPattern(pattern))
        .filter(filterExpired(Instant.now()).negate())
        .map(Tuple2::_1)
        .map(DatabaseKey::getValue);
    return convert(keys);
  }
  
  private GlobPattern createPattern(SafeString param) {
    return new GlobPattern(param.toString());
  }

  private Predicate<? super Tuple2<DatabaseKey, DatabaseValue>> filterExpired(Instant now) {
    return entry -> entry._2().isExpired(now);
  }

  private Predicate<? super Tuple2<DatabaseKey, DatabaseValue>> matchPattern(GlobPattern pattern) {
    return entry -> pattern.match(entry._1().toString());
  }
}
