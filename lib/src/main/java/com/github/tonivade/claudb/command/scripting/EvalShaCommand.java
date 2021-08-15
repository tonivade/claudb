/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

import com.github.tonivade.claudb.DBServerState;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.SafeString;

@Command("evalsha")
@ParamLength(2)
public class EvalShaCommand extends AbstractEvalCommand {

  @Override
  protected Option<SafeString> script(Request request) {
    DBServerState server = getServerState(request.getServerContext());
    return server.getScript(request.getParam(0));
  }
}
