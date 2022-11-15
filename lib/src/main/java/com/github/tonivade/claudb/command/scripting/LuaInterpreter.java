/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.status;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.lang.String.valueOf;
import static java.util.Objects.requireNonNull;
import com.github.tonivade.purefun.Pattern1;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.script.LuajContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LuaInterpreter {

  private static final Logger log = LoggerFactory.getLogger(LuaInterpreter.class);

  private final LuaRedisBinding redis;

  protected LuaInterpreter(LuaRedisBinding binding) {
    this.redis = requireNonNull(binding);
  }

  public static LuaInterpreter buildFor(Request request) {
    return new LuaInterpreter(createBinding(request));
  }

  public RedisToken execute(SafeString script, List<SafeString> keys, List<SafeString> params) {
    try {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("luaj");
      engine.setContext(createContext());
      engine.put("redis", createBinding(redis));
      engine.put("KEYS", toArray(keys));
      engine.put("ARGV", toArray(params));
      return convert(engine.eval(script.toString()));
    } catch (LuaError e) {
      log.error("lua error", e);
      return error(e.getMessage());
    } catch (ScriptException e) {
      log.error("script error", e);
      return error(e.getMessage());
    }
  }

  private LuajContext createContext() {
    LuajContext sandbox = new LuajContext();
    sandbox.globals.set("package", LuaValue.NIL);
    sandbox.globals.set("require", LuaValue.NIL);
    sandbox.globals.set("io", LuaValue.NIL);
    sandbox.globals.set("os", LuaValue.NIL);
    sandbox.globals.set("luajava", LuaValue.NIL);
    return sandbox;
  }

  private LuaValue createBinding(LuaRedisBinding redis) {
    LuaTable binding = LuaValue.tableOf();
    binding.set("call", redis);
    return binding;
  }

  private RedisToken convert(Object result) {
    return Pattern1.<Object, RedisToken>build()
        .when(LuaTable.class)
          .then(this::convertLuaTable)
        .when(LuaNumber.class)
          .then(this::convertLuaNumber)
        .when(LuaBoolean.class)
          .then(this::convertLuaBoolean)
        .when(LuaString.class)
          .then(this::convertLuaString)
        .when(Number.class)
          .then(this::convertNumber)
        .when(String.class)
          .then(this::convertString)
        .when(Boolean.class)
          .then(this::convertBoolean)
        .otherwise()
          .then(this::convertUnknown)
        .apply(result);
  }

  private RedisToken convertLuaTable(LuaTable value) {
    List<RedisToken> tokens = new ArrayList<>();
    if (value.keyCount() == 1 && value.get("ok") != LuaValue.NIL) {
      return status(value.get("ok").checkjstring());
    }
    for (LuaValue key : value.keys()) {
      tokens.add(convert(value.get(key)));
    }
    return array(tokens);
  }

  private RedisToken convertLuaNumber(LuaNumber value) {
    return integer(value.toint());
  }

  private RedisToken convertLuaString(LuaString value) {
    return string(value.tojstring());
  }

  private RedisToken convertLuaBoolean(LuaBoolean value) {
    return value.toboolean() ? integer(1) : nullString();
  }

  private RedisToken convertNumber(Number number) {
    return integer(number.intValue());
  }

  private RedisToken convertString(String string) {
    return string(string);
  }

  private RedisToken convertBoolean(Boolean value) {
    return Boolean.TRUE.equals(value) ? integer(1) : nullString();
  }

  private RedisToken convertUnknown(Object value) {
    return value != null ? string(valueOf(value)) : nullString();
  }

  private Object[] toArray(List<SafeString> keys) {
    return keys.stream().map(SafeString::toString).toArray(String[]::new);
  }

  private static LuaRedisBinding createBinding(Request request) {
    return new LuaRedisBinding(createLibrary(request));
  }

  private static RedisLibrary createLibrary(Request request) {
    return new RedisLibrary(request.getServerContext(), request.getSession());
  }
}
