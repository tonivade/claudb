/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.scripting;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.resp.util.Precondition.checkNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import com.github.tonivade.resp.protocol.SafeString;

import gnu.lists.IString;
import gnu.mapping.ProcedureN;

public class SchemeRedisBinding {

  private final RedisLibrary redis;

  public SchemeRedisBinding(RedisLibrary redis) {
    this.redis = checkNonNull(redis);
  }

  public ProcedureN call() {
    return new ProcedureImpl(redis::call);
  }

  public ProcedureN pcall() {
    return new ProcedureImpl(redis::pcall);
  }

  private static final class ProcedureImpl extends ProcedureN {

    private final BiFunction<SafeString, SafeString[], Object> task;

    private ProcedureImpl(BiFunction<SafeString, SafeString[], Object> task) {
      this.task = checkNonNull(task);
    }

    @Override
    public Object applyN(Object[] args) {
      return task.apply(readCommand(args), readArguments(args));
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
      if (object instanceof IString) {
        return safeString(object.toString());
      }
      throw new IllegalArgumentException(object.getClass() + "=" + object);
    }
  }
}
