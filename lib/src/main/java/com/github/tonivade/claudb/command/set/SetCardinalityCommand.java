/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.set;

import static com.github.tonivade.resp.protocol.RedisToken.integer;

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
import java.util.Set;

@ReadOnly
@Command("scard")
@ParamLength(1)
@ParamType(DataType.SET)
public class SetCardinalityCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    Set<SafeString> set = db.getSet(request.getParam(0));
    return integer(set.size());
  }
}
