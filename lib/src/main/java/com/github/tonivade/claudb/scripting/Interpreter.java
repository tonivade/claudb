/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import com.github.tonivade.claudb.DBConfig;
import com.github.tonivade.claudb.DBConfig.Engine;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.List;

@FunctionalInterface
public interface Interpreter {

  public static final String CONFIG = "config";

  RedisToken execute(SafeString script, List<SafeString> keys, List<SafeString> params);

  static Interpreter nullEngine() {
    return (script, keys, params) -> error("interpreter disabled");
  }

  static Interpreter build(Request request) {
    Engine engine = request.getServerContext().<DBConfig>getValue(CONFIG).map(DBConfig::getEngine).orElse(Engine.NULL);

    if (engine == Engine.JAVASCRIPT) {
      return NashornInterpreter.buildFor(request);
    }
    if (engine == Engine.LUAJ) {
      return LuaInterpreter.buildFor(request);
    }
    return nullEngine();
  }
}
