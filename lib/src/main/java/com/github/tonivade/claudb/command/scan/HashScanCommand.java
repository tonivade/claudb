/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scan;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.resp.protocol.RedisToken.array;
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
import java.util.Map;
import java.util.stream.Stream;

@Command("hscan")
@ParamLength(2)
@ParamType(DataType.HASH)
public class HashScanCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    SafeString key = request.getParam(0);
    int cursor = Integer.parseInt(request.getParam(1).toString());
    Map<SafeString, SafeString> value = db.getOrDefault(safeKey(key), DatabaseValue.EMPTY_HASH).getHash();

    GlobPattern pattern = request.getOptionalParam(2).map(Object::toString).map(GlobPattern::new)
      .orElse(null);

    List<RedisToken> result = value.entrySet().stream()
      .filter(entry -> pattern == null || pattern.match(entry.getKey().toString()))
      .skip(cursor).limit(10)
      .flatMap(entry -> Stream.of(string(entry.getKey()), string(entry.getValue())))
      .collect(toList());
    if (result.isEmpty()) {
      return array(string("0"), array());
    }
    return array(string(String.valueOf(cursor + (result.size() / 2))), array(result));
  }
}
