package com.github.tonivade.tinydb.command.scripting;

import java.util.NoSuchElementException;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.TinyDBServerState;

@Command("evalsha")
@ParamLength(2)
public class EvalShaCommand extends AbstractEvalCommand {

  @Override
  protected SafeString script(IRequest request) {
    TinyDBServerState server = getServerState(request.getServerContext());
    return server.getScript(request.getParam(0)).orElseThrow(NoSuchElementException::new);
  }

}
