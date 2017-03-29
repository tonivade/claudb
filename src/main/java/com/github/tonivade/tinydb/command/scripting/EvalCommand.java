/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.ITinyDBCommand;
import com.github.tonivade.tinydb.data.IDatabase;

@Command("eval")
@ParamLength(2)
public class EvalCommand implements ITinyDBCommand {

  @Override
  public RedisToken execute(IDatabase db, IRequest request) {
    SafeString script = request.getParam(0);

    int numParams = parseInt(request.getParam(1).toString());

    if (numParams + 2 > request.getLength()) {
      return RedisToken.error("invalid number of arguments");
    }
    List<SafeString> params = request.getParams().stream().skip(2).collect(toList());
    List<SafeString> keys = readParams(numParams, params);
    List<SafeString> argv = readArguments(numParams, params);
    return createInterperterFor(request).execute(script, keys, argv);
  }

  private List<SafeString> readParams(int numParams, List<SafeString> params) {
    List<SafeString> keys = new LinkedList<>();
    for (int i = 0; i < numParams; i++) {
      keys.add(params.get(i));
    }
    return keys;
  }

  private List<SafeString> readArguments(int numParams, List<SafeString> params) {
    List<SafeString> argv = new LinkedList<>();
    for (int i = numParams; i < params.size(); i++) {
      argv.add(params.get(i));
    }
    return argv;
  }

  private LuaInterpreter createInterperterFor(IRequest request) {
    return new LuaInterpreter(createBinding(request));
  }

  private RedisBinding createBinding(IRequest request) {
    return new RedisBinding(createLibrary(request));
  }

  private RedisLibrary createLibrary(IRequest request) {
    return new RedisLibrary(request.getServerContext(), request.getSession());
  }

}
