/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.set;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

@ReadOnly
@Command("sunion")
@ParamLength(2)
@ParamType(DataType.SET)
public class SetUnionCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    ImmutableSet<SafeString> result = db.getSet(request.getParam(0));
    for (SafeString param : request.getParams().asList().tail()) {
      result = result.union(db.getSet(param));
    }
    return convert(result);
  }
}
