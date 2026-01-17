/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scan;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.stream.Collectors.toList;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.claudb.glob.GlobPattern;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

import java.util.List;
import java.util.Set;

@Command("sscan")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetScanCommand implements DBCommand {

  private final ScanParams params = new ScanParams(2);

  @Override
  public RedisToken execute(Database db, Request request) {
    SafeString key = request.getParam(0);
    int cursor = Integer.parseInt(request.getParam(1).toString());
    Set<SafeString> value = db.getOrDefault(safeKey(key), DatabaseValue.EMPTY_SET).getSet();

    try {
      GlobPattern pattern = params.parsePattern(request);
      int count = params.parseCount(request);

      List<RedisToken> result = value.stream()
        .filter(item -> pattern == null || pattern.match(item.toString()))
        .skip(cursor).limit(count)
        .map(RedisToken::string)
        .collect(toList());
      if (result.isEmpty()) {
        return array(string("0"), array());
      }
      return array(string(String.valueOf(cursor + result.size())), array(result));
    } catch (IllegalArgumentException e) {
      return error("ERR syntax error");
    }
  }
}
