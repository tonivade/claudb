/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command.server;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.responseOk;
import static java.lang.Integer.parseInt;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.Database;

@ReadOnly
@Command("select")
@ParamLength(1)
public class SelectCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    try {
      getSessionState(request.getSession()).setCurrentDB(parseCurrentDB(request));
      return responseOk();
    } catch (NumberFormatException e) {
      return error("ERR invalid DB index");
    }
  }

  private int parseCurrentDB(Request request) {
    return parseInt(request.getParam(0).toString());
  }
}
