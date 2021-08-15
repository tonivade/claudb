/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.key;

import java.time.Instant;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.claudb.glob.GlobPattern;
import com.github.tonivade.purefun.Matcher1;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

@ReadOnly
@Command("keys")
@ParamLength(1)
public class KeysCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    GlobPattern pattern = createPattern(request.getParam(0));
    ImmutableSet<SafeString> keys = db.entrySet()
        .filter(matchPattern(pattern))
        .filter(filterExpired(Instant.now()).negate())
        .map(Tuple2::get1)
        .map(DatabaseKey::getValue);
    return convert(keys);
  }

  private GlobPattern createPattern(SafeString param) {
    return new GlobPattern(param.toString());
  }

  private Matcher1<Tuple2<DatabaseKey, DatabaseValue>> filterExpired(Instant now) {
    return entry -> entry.get2().isExpired(now);
  }

  private Matcher1<Tuple2<DatabaseKey, DatabaseValue>> matchPattern(GlobPattern pattern) {
    return entry -> pattern.match(entry.get1().toString());
  }
}
