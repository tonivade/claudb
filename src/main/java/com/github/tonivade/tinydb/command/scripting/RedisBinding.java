/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.RedisToken.IntegerRedisToken;
import com.github.tonivade.resp.protocol.RedisToken.StatusRedisToken;
import com.github.tonivade.resp.protocol.RedisToken.StringRedisToken;
import com.github.tonivade.resp.protocol.RedisToken.UnknownRedisToken;
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
    return Match(token).of(Case(instanceOf(StringRedisToken.class), this::toLuaString),
                           Case(instanceOf(StatusRedisToken.class), this::toLuaString),
                           Case(instanceOf(ArrayRedisToken.class), this::toLuaTable),
                           Case(instanceOf(IntegerRedisToken.class), this::toLuaNumber),
                           Case(instanceOf(UnknownRedisToken.class), this::toLuaString));
  }

  private LuaInteger toLuaNumber(IntegerRedisToken value) {
    return LuaInteger.valueOf(Integer.parseInt(value.getValue().toString()));
  }

  private LuaTable toLuaTable(ArrayRedisToken value) {
    LuaTable table = LuaValue.tableOf();
    int i = 0;
    for (RedisToken<?> token : value.getValue()) {
      table.set(++i, convert(token));
    }
    return table;
  }

  private LuaString toLuaString(RedisToken<?> value) {
    return LuaString.valueOf(value.getValue().toString());
  }
}
