/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

import static com.github.tonivade.purefun.Matcher1.instanceOf;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import com.github.tonivade.purefun.Pattern1;
import com.github.tonivade.resp.protocol.AbstractRedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.IntegerRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.StatusRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.StringRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.UnknownRedisToken;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

public class RedisBinding extends VarArgFunction {

  private final RedisLibrary redis;

  public RedisBinding(RedisLibrary redis) {
    this.redis = requireNonNull(redis);
  }

  @Override
  public Varargs invoke(Varargs args) {
    return convert(redis.call(readCommand(args), readArguments(args)));
  }

  private SafeString[] readArguments(Varargs args) {
    List<SafeString> params = new ArrayList<>();
    if (args.narg() > 1) {
      for (int i = 1; i < args.narg(); i++) {
        params.add(toSafeString(args.checkstring(i + 1)));
      }
    }
    return params.toArray(new SafeString[0]);
  }

  private SafeString readCommand(Varargs args) {
    return toSafeString(args.checkstring(1));
  }

  private SafeString toSafeString(LuaString value)
  {
    return new SafeString(value.m_bytes);
  }

  private LuaValue convert(RedisToken token) {
    return Pattern1.<RedisToken, LuaValue>build()
        .when(instanceOf(StringRedisToken.class))
          .then(string -> toLuaString((StringRedisToken) string))
        .when(instanceOf(StatusRedisToken.class))
          .then(status -> toLuaString((StatusRedisToken) status))
        .when(instanceOf(ArrayRedisToken.class))
          .then(array -> toLuaTable((ArrayRedisToken) array))
        .when(instanceOf(IntegerRedisToken.class))
          .then(integer -> toLuaNumber((IntegerRedisToken) integer))
        .when(instanceOf(UnknownRedisToken.class))
          .then(unknown -> toLuaString((UnknownRedisToken) unknown))
        .apply(token);
  }

  private LuaValue toLuaNumber(IntegerRedisToken value) {
    Integer integer = value.getValue();
    if (integer == null) {
      return LuaValue.NIL;
    }
    return LuaInteger.valueOf(integer);
  }

  private LuaTable toLuaTable(ArrayRedisToken value) {
    LuaTable table = LuaValue.tableOf();
    int i = 0;
    for (RedisToken token : value.getValue()) {
      table.set(++i, convert(token));
    }
    return table;
  }

  private LuaValue toLuaString(StringRedisToken value) {
    SafeString string = value.getValue();
    if (string == null) {
      return LuaValue.NIL;
    }
    return LuaString.valueOf(string.getBytes());
  }

  private LuaValue toLuaString(StatusRedisToken value) {
    String string = value.getValue();
    if (string == null) {
      return LuaValue.NIL;
    }
    return LuaString.valueOf(string);
  }

  private LuaValue toLuaString(UnknownRedisToken value) {
    SafeString string = value.getValue();
    if (string == null) {
      return LuaValue.NIL;
    }
    return LuaString.valueOf(string.getBytes());
  }
}
