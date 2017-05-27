/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;

import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.data.Database;

abstract class AbstractEvalCommand implements TinyDBCommand {

  @Override
  public RedisToken<?> execute(Database db, Request request) {
    int numParams = parseInt(request.getParam(1).toString());

    if (numParams + 2 > request.getLength()) {
      return RedisToken.error("invalid number of arguments");
    }
    List<SafeString> params = request.getParams().stream().skip(2).collect(toList());
    List<SafeString> keys = readParams(numParams, params);
    List<SafeString> argv = readArguments(numParams, params);
    return LuaInterpreter.buildFor(request).execute(script(request), keys, argv);
  }

  protected abstract SafeString script(Request request);

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
