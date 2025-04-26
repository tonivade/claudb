/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.util.Precondition.checkNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.List;
import java.util.Map;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

class NashornInterpreter implements Interpreter {

  private final NashornRedisBinding binding;

  protected NashornInterpreter(NashornRedisBinding binding) {
    this.binding = checkNonNull(binding);
  }

  static NashornInterpreter buildFor(Request request) {
    return new NashornInterpreter(new NashornRedisBinding(createLibrary(request)));
  }

  @Override
  public RedisToken execute(SafeString script, List<SafeString> keys, List<SafeString> params) {
    try {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("nashorn");
      engine.put("redis", binding);
      engine.put("KEYS", keys);
      engine.put("ARGV", params);
      return convert(engine.eval(script.toString()));
    } catch (ScriptException e) {
      return error(e.getMessage());
    }
  }

  private RedisToken convert(Object value) {
    if (value instanceof SafeString) {
      return RedisToken.string((SafeString) value);
    }
    if (value instanceof Integer) {
      return RedisToken.integer(((Integer) value));
    }
    if (value instanceof Boolean) {
      return Boolean.TRUE.equals(value) ? integer(1) : nullString();
    }
    if (value instanceof RedisToken) {
      return (RedisToken) value;
    }
    if (value instanceof Bindings) {
      return toArray((Bindings) value);
    }
    throw new IllegalArgumentException(value.getClass() + "=" + value);
  }

  private RedisToken toArray(Bindings value) {
    return value.entrySet().stream()
      .sorted(Map.Entry.comparingByKey())
      .map(Map.Entry::getValue).map(this::convert)
      .collect(collectingAndThen(toList(), RedisToken::array));
  }


  private static RedisLibrary createLibrary(Request request) {
    return new RedisLibrary(request.getServerContext(), request.getSession());
  }
}
