/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.set;

import static java.util.stream.Collectors.toList;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.HashSet;
import java.util.Set;

@ReadOnly
@Command("sdiff")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetDifferenceCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    Set<SafeString> result = new HashSet<>(db.getSet(request.getParam(0)));
    for (SafeString param : request.getParamsAsStream().skip(1).collect(toList())) {
      result.removeAll(db.getSet(param));
    }
    return convert(result);
  }
}
