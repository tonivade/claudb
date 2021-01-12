/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

import static com.github.tonivade.purefun.Matcher1.instanceOf;
import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.lang.String.valueOf;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import com.github.tonivade.purefun.Pattern1;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

public final class LuaInterpreter {

  private final RedisBinding redis;

  protected LuaInterpreter(RedisBinding binding) {
    this.redis = requireNonNull(binding);
  }

  public static LuaInterpreter buildFor(Request request) {
    return new LuaInterpreter(createBinding(request));
  }

  public RedisToken execute(SafeString script, List<SafeString> keys, List<SafeString> params) {
    try {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("luaj");
      engine.put("redis", createBinding(redis));
      engine.put("KEYS", toArray(keys));
      engine.put("ARGV", toArray(params));
      return convert(engine.eval(script.toString()));
    } catch (ScriptException e) {
      return error(e.getMessage());
    }
  }

  private LuaValue createBinding(RedisBinding redis) {
    LuaTable binding = LuaTable.tableOf();
    binding.set("call", redis);
    return binding;
  }

  private RedisToken convert(Object result) {
    return Pattern1.<Object, RedisToken>build()
        .when(instanceOf(LuaTable.class))
          .then(table -> convertLuaTable((LuaTable) table))
        .when(instanceOf(LuaNumber.class))
          .then(number -> convertLuaNumber((LuaNumber) number))
        .when(instanceOf(LuaBoolean.class))
          .then(boolean_ -> convertLuaBoolean((LuaBoolean) boolean_))
        .when(instanceOf(LuaString.class))
          .then(string -> convertLuaString((LuaString) string))
        .when(instanceOf(Number.class))
          .then(number -> convertNumber((Number) number))
        .when(instanceOf(String.class))
          .then(string -> convertString((String) string))
        .when(instanceOf(Boolean.class))
          .then(boolean_ -> convertBoolean((Boolean) boolean_))
        .otherwise()
          .then(this::convertUnknown)
        .apply(result);
  }

  private RedisToken convertLuaTable(LuaTable value) {
    List<RedisToken> tokens = new ArrayList<>();
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
    return value ? integer(1) : nullString();
  }

  private RedisToken convertUnknown(Object value) {
    return value != null ? string(valueOf(value)) : nullString();
  }

  private Object[] toArray(List<SafeString> keys) {
    return keys.stream().map(SafeString::toString).toArray(String[]::new);
  }

  private static RedisBinding createBinding(Request request) {
    return new RedisBinding(createLibrary(request));
  }

  private static RedisLibrary createLibrary(Request request) {
    return new RedisLibrary(request.getServerContext(), request.getSession());
  }
}
