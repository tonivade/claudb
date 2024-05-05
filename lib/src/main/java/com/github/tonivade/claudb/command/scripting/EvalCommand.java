/*
 * Copyright (c) 2015-2024, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.Optional;

@Command("eval")
@ParamLength(2)
public class EvalCommand extends AbstractEvalCommand {

  @Override
  protected Optional<SafeString> script(Request request) {
    return request.getOptionalParam(0);
  }
}
