/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

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
import com.github.tonivade.resp.protocol.AbstractRedisToken.ErrorRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.IntegerRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.StatusRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.StringRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.UnknownRedisToken;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

public class LuaRedisBinding {

  private static final String ERROR = "error";
  private static final String OK = "ok";

  private final RedisLibrary redis;

  public LuaRedisBinding(RedisLibrary redis) {
    this.redis = requireNonNull(redis);
  }

  public VarArgFunction call() {
    return new VarArgFunction() {
      @Override
      public Varargs invoke(Varargs args) {
        return convert(redis.call(readCommand(args), readArguments(args)));
      }
    };
  }

  public VarArgFunction pcall() {
    return new VarArgFunction() {
      @Override
      public Varargs invoke(Varargs args) {
        return convert(redis.pcall(readCommand(args), readArguments(args)));
      }
    };
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

  private SafeString toSafeString(LuaString value) {
    return new SafeString(value.m_bytes);
  }

  private LuaValue convert(RedisToken token) {
    return Pattern1.<RedisToken, LuaValue>build()
        .when(StringRedisToken.class)
          .then(this::toLuaString)
        .when(StatusRedisToken.class)
          .then(this::toLuaStatus)
        .when(ArrayRedisToken.class)
          .then(this::toLuaTable)
        .when(IntegerRedisToken.class)
          .then(this::toLuaNumber)
        .when(ErrorRedisToken.class)
          .then(this::toLuaError)
        .when(UnknownRedisToken.class)
          .then(this::toLuaString)
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

  private LuaValue toLuaStatus(StatusRedisToken value) {
    String string = value.getValue();
    if (string == null) {
      return LuaValue.NIL;
    }
    LuaTable table = LuaValue.tableOf();
    table.set(LuaValue.valueOf(OK), LuaValue.valueOf(string));
    return table;
  }

  private LuaValue toLuaError(ErrorRedisToken value) {
    String string = value.getValue();
    if (string == null) {
      return LuaValue.NIL;
    }
    LuaTable table = LuaValue.tableOf();
    table.set(LuaValue.valueOf(ERROR), LuaValue.valueOf(string));
    return table;
  }

  private LuaValue toLuaString(UnknownRedisToken value) {
    SafeString string = value.getValue();
    if (string == null) {
      return LuaValue.NIL;
    }
    return LuaString.valueOf(string.getBytes());
  }
}
