/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.scripting;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.resp.util.Precondition.checkNonNull;

import java.util.ArrayList;
import java.util.List;
import com.github.tonivade.resp.protocol.SafeString;

public class NashornRedisBinding {

  private final RedisLibrary redis;

  public NashornRedisBinding(RedisLibrary redis) {
    this.redis = checkNonNull(redis);
  }

  public Object call(Object... params) {
    return redis.call(readCommand(params), readArguments(params));
  }

  public Object pcall(Object... params) {
    return redis.pcall(readCommand(params), readArguments(params));
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
    if (object instanceof String) {
      return safeString(object.toString());
    }
    throw new IllegalArgumentException(object.getClass() + "=" + object);
  }
}
