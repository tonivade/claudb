/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static javaslang.API.$;
import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

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

import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

import javaslang.control.Try;

public class LuaInterpreter {

  private RedisBinding redis;

  public LuaInterpreter(RedisBinding binding) {
    this.redis = binding;
  }

  public RedisToken execute(SafeString script, List<SafeString> keys, List<SafeString> params) {
    return Try.of(() -> run(script, keys, params)).getOrElse(error(IResponse.RESULT_ERROR));
  }

  private RedisToken run(SafeString script, List<SafeString> keys, List<SafeString> params) throws ScriptException {
    try {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("luaj");
      engine.put("redis", createBinding(redis));
      engine.put("KEYS", toArray(keys));
      engine.put("ARGV", toArray(params));
      return convert(engine.eval(script.toString()));
    } catch (ScriptException e) {
      throw e;
    } catch (RuntimeException e) {
      throw new ScriptException(e);
    }
  }

  private LuaValue createBinding(RedisBinding redis) {
    LuaTable binding = LuaTable.tableOf();
    binding.set("call", redis);
    return binding;
  }

  private RedisToken convert(Object result) {
    return Match(result).of(Case(instanceOf(LuaTable.class), this::convertLuaTable),
                            Case(instanceOf(LuaNumber.class), this::convertLuaNumber),
                            Case(instanceOf(LuaBoolean.class), this::convertLuaBoolean),
                            Case(instanceOf(LuaString.class), this::convertLuaString),
                            Case(instanceOf(Number.class), this::convertNumber),
                            Case(instanceOf(String.class), this::convertString),
                            Case(instanceOf(Boolean.class), this::convertBoolean),
                            Case($(), this::convertUnknown));
  }

  private RedisToken convertLuaTable(LuaTable value) {
    List<RedisToken> tokens = new ArrayList<>();
    for (LuaValue key : value.keys())
    {
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
    return value.toboolean() ? integer(1) : string(SafeString.EMPTY_STRING);
  }

  private RedisToken convertNumber(Number number) {
    return integer(number.intValue());
  }

  private RedisToken convertString(String string) {
    return string(string);
  }

  private RedisToken convertBoolean(Boolean value) {
    return value.booleanValue() ? integer(1) : string(SafeString.EMPTY_STRING);
  }
  
  private RedisToken convertUnknown(Object value) {
    return string(String.valueOf(value));
  }

  private Object[] toArray(List<SafeString> keys) {
    return keys.stream().map(SafeString::toString).toArray(String[]::new);
  }

}
