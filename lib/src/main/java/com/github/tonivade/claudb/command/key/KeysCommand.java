/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.key;

import static java.util.stream.Collectors.toSet;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.claudb.glob.GlobPattern;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@ReadOnly
@Command("keys")
@ParamLength(1)
public class KeysCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    GlobPattern pattern = createPattern(request.getParam(0));
    Set<SafeString> keys = db.entrySet().stream()
        .filter(matchPattern(pattern))
        .filter(filterExpired(Instant.now()).negate())
        .map(Map.Entry::getKey)
        .map(DatabaseKey::getValue)
        .collect(toSet());
    return convert(keys);
  }

  private GlobPattern createPattern(SafeString param) {
    return new GlobPattern(param.toString());
  }

  private Predicate<Map.Entry<DatabaseKey, DatabaseValue>> filterExpired(Instant now) {
    return entry -> entry.getValue().isExpired(now);
  }

  private Predicate<Map.Entry<DatabaseKey, DatabaseValue>> matchPattern(GlobPattern pattern) {
    return entry -> pattern.match(entry.getKey().toString());
  }
}
