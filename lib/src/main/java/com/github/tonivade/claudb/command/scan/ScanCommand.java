/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scan;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.stream.Collectors.toList;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import java.util.List;

@Command("scan")
@ParamLength(1)
@ParamType(DataType.STRING)
public class ScanCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    int cursor = Integer.parseInt(request.getParam(0).toString());
    List<RedisToken> result = db.entrySet().stream()
      .filter(entry -> entry.getValue().getType() == DataType.STRING)
      .skip(cursor).limit(10)
      .map(entry -> entry.getKey().getValue())
      .map(RedisToken::string)
      .collect(toList());
    if (result.isEmpty()) {
      return array(string("0"));
    }
    return array(string(String.valueOf(cursor + result.size())), array(result));
  }
}
