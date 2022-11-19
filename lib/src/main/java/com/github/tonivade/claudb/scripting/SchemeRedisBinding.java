/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.scripting;

import static java.util.Objects.requireNonNull;
import com.github.tonivade.purefun.Pattern1;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.ArrayList;
import java.util.List;
import gnu.lists.IString;
import gnu.mapping.ProcedureN;

public class SchemeRedisBinding {

  private final RedisLibrary redis;

  public SchemeRedisBinding(RedisLibrary redis) {
    this.redis = requireNonNull(redis);
  }

  public ProcedureN call() {
    return new ProcedureN() {
      @Override
      public Object applyN(Object[] args) {
        return redis.call(readCommand(args), readArguments(args));
      }
    };
  }

  public ProcedureN pcall() {
    return new ProcedureN() {
      @Override
      public Object applyN(Object[] args) {
        return redis.pcall(readCommand(args), readArguments(args));
      }
    };
  }

  private SafeString readCommand(Object[] args) {
    return toSafeString(args[0]);
  }

  private SafeString[] readArguments(Object[] args) {
    List<SafeString> params = new ArrayList<>();
    if (args.length > 1) {
      for (int i = 1; i < args.length; i++) {
        params.add(toSafeString(args[i]));
      }
    }
    return params.toArray(new SafeString[0]);
  }

  private SafeString toSafeString(Object object) {
    return Pattern1.<Object, SafeString>build()
      .when(IString.class)
        .then(s -> SafeString.safeString(s.toString()))
      .otherwise()
        .then(x -> {
          throw new IllegalArgumentException(x.getClass() + "=" + x);
        })
      .apply(object);
  }
}
