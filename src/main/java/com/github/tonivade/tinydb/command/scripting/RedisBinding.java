/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static javaslang.API.$;
import static javaslang.API.Case;
import static javaslang.API.Match;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisTokenType;
import com.github.tonivade.resp.protocol.SafeString;

public class RedisBinding extends VarArgFunction {

  private RedisLibrary redis;

  public RedisBinding(RedisLibrary redis) {
    this.redis = redis;
  }

  @Override
  public Varargs invoke(Varargs args) {
    return convert(redis.call(readCommand(args), readArguments(args)));
  }

  private SafeString[] readArguments(Varargs args) {
    List<SafeString> params = new ArrayList<>();
    if (args.narg() > 1) {
      for(int i = 1; i < args.narg(); i++) {
        params.add(safeString(args.tojstring(i + 1)));
      }
    }
    return params.stream().toArray(SafeString[]::new);
  }

  private SafeString readCommand(Varargs args) {
    return safeString(args.checkjstring(1));
  }

  private LuaValue convert(RedisToken token) {
    return Match(token).of(Case(isType(RedisTokenType.STRING), this::toLuaString),
                           Case(isType(RedisTokenType.STATUS), this::toLuaString),
                           Case(isType(RedisTokenType.ARRAY), this::toLuaTable),
                           Case(isType(RedisTokenType.INTEGER), this::toLuaNumber),
                           Case($(), value -> toLuaTable(value)));
  }

  private LuaInteger toLuaNumber(RedisToken value) {
    return LuaInteger.valueOf(Integer.parseInt(value.getValue().toString()));
  }

  private LuaTable toLuaTable(RedisToken value) {
    LuaTable table = LuaValue.tableOf();
    int i = 0;
    for (RedisToken token : value.<List<RedisToken>>getValue()) {
      table.set(++i, convert(token));
    }
    return table;
  }

  private LuaString toLuaString(RedisToken value) {
    return LuaString.valueOf(value.getValue().toString());
  }

  private Predicate<? super RedisToken> isType(RedisTokenType type) {
    return value -> value.getType() == type;
  }
}
