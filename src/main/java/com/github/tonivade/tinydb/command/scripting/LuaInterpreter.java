/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.RedisToken.error;
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

  public RedisToken execute(SafeString script, List<SafeString> keys, List<SafeString> params) {
    return Try.of(() -> run(script, keys, params)).getOrElse(error(IResponse.RESULT_ERROR));
  }

  private RedisToken run(SafeString script, List<SafeString> keys, List<SafeString> params) throws ScriptException {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("luaj");
    engine.put("KEYS", toArray(keys));
    engine.put("ARGV", toArray(params));
    return convert(engine.eval(script.toString()));
  }

  private RedisToken convert(Object result) {
    return Match(result).of(Case(instanceOf(LuaTable.class), this::convertTable),
                            Case(instanceOf(LuaNumber.class), this::convertNumber),
                            Case(instanceOf(LuaBoolean.class), this::convertBoolean),
                            Case(instanceOf(LuaString.class), this::convertString),
                            Case($(), this::otherwise));
  }

  private RedisToken convertTable(LuaTable value) {
    List<RedisToken> tokens = new ArrayList<>();
    for (LuaValue key : value.keys())
    {
      tokens.add(convert(value.get(key)));
    }
    return RedisToken.array(tokens);
  }

  private RedisToken convertNumber(LuaNumber value) {
    return RedisToken.integer(value.toint());
  }

  private RedisToken convertString(LuaString value) {
    return RedisToken.string(value.tojstring());
  }

  private RedisToken convertBoolean(LuaBoolean value) {
    return value.toboolean() ? RedisToken.string(SafeString.EMPTY_STRING) : RedisToken.integer(1);
  }

  private RedisToken otherwise(Object value)
  {
    return RedisToken.string(value.toString());
  }

  private Object[] toArray(List<SafeString> keys) {
    return keys.stream().map(SafeString::toString).toArray(String[]::new);
  }

}
