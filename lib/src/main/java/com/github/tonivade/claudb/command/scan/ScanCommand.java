/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scan;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.glob.GlobPattern;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;

@Command("scan")
@ParamLength(1)
public class ScanCommand implements DBCommand {

  private static final int DEFAULT_COUNT = 10;
  private static final String COUNT = "count";
  private static final String MATCH = "match";

  @Override
  public RedisToken execute(Database db, Request request) {
    int cursor = Integer.parseInt(request.getParam(0).toString());
    
    GlobPattern pattern = parsePattern(request);
    int count = parseCount(request);

    List<RedisToken> result = db.entrySet().stream()
      .filter(entry -> pattern == null || pattern.match(entry.getKey().toString()))
      .skip(cursor).limit(count)
      .map(entry -> entry.getKey().getValue())
      .map(RedisToken::string)
      .collect(toList());
    if (result.isEmpty()) {
      return array(string("0"), array());
    }
    return array(string(String.valueOf(cursor + result.size())), array(result));
  }

  private int parseCount(Request request) {
    int count = DEFAULT_COUNT;
    for (int i = 1; i < request.getLength(); i++) {
      if (request.getParam(i).toString().equalsIgnoreCase(COUNT)) {
        count = request.getOptionalParam(++i).map(Object::toString).map(Integer::parseInt)
          .orElse(DEFAULT_COUNT);
      }
    }
    return count;
  }

  private GlobPattern parsePattern(Request request) {
    GlobPattern pattern = null;
    for (int i = 1; i < request.getLength(); i++) {
      if (request.getParam(i).toString().equalsIgnoreCase(MATCH)) {
        pattern = request.getOptionalParam(++i).map(Object::toString).map(GlobPattern::new)
          .orElse(null);
      }
    }
    return pattern;
  }
}
