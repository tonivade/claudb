/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

abstract class AbstractEvalCommand implements DBCommand {

  @Override
  public RedisToken execute(Database db, Request request) {
    return script(request).map(script -> execute(request, script))
        .getOrElse(error("NOSCRIPT No matching script. Please use EVAL"));
  }

  private RedisToken execute(Request request, SafeString script) {
    int numParams = parseInt(request.getParam(1).toString());
    if (numParams + 2 > request.getLength()) {
      return error("invalid number of arguments");
    }
    List<SafeString> params = request.getParams().stream().skip(2).collect(toList());
    List<SafeString> keys = readParams(numParams, params);
    List<SafeString> argv = readArguments(numParams, params);
    return LuaInterpreter.buildFor(request).execute(script, keys, argv);
  }

  protected abstract Option<SafeString> script(Request request);

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
}
