/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.string;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.string;
import static com.github.tonivade.resp.protocol.RedisToken.integer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
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
    Set<Map.Entry<SafeString, SafeString>> pairs = toPairs(request);
    if (noneExists(db, pairs)) {
      pairs.forEach(entry -> db.put(safeKey(entry.getKey()), string(entry.getValue())));
      return integer(1);
    }
    return integer(0);
  }

  private boolean noneExists(Database db, Set<Map.Entry<SafeString, SafeString>> pairs) {
    return pairs.stream()
        .map(Map.Entry::getKey)
        .map(DatabaseKey::safeKey)
        .noneMatch(db::containsKey);
  }

  private Set<Map.Entry<SafeString, SafeString>> toPairs(Request request) {
    Set<Map.Entry<SafeString, SafeString>> pairs = new HashSet<>();
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
