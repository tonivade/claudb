/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

import static com.github.tonivade.purefun.Function1.fail;
import static com.github.tonivade.purefun.Function1.identity;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static java.util.Objects.requireNonNull;
import com.github.tonivade.purefun.Pattern1;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import gnu.lists.FVector;
import gnu.lists.GVector;
import gnu.math.IntNum;

public class SchemeInterpreter {

  private final SchemeRedisBinding redis;

  protected SchemeInterpreter(SchemeRedisBinding redis) {
    this.redis = requireNonNull(redis);
  }

  public static SchemeInterpreter buildFor(Request request) {
    return new SchemeInterpreter(new SchemeRedisBinding(createLibrary(request)));
  }

  public RedisToken execute(SafeString script, List<SafeString> keys, List<SafeString> params) {
    try {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("scheme");
      engine.put("call-redis", redis);
      engine.put("KEYS", toVector(keys));
      engine.put("ARGV", toVector(params));
      return convert(engine.eval(script.toString()));
    } catch (ScriptException e) {
      return error(e.getMessage());
    }
  }

  private GVector<SafeString> toVector(List<SafeString> keys) {
    return new FVector<>(keys);
  }

  private RedisToken convert(Object eval) {
    System.out.println(eval);
    System.out.println(eval.getClass());
    return Pattern1.<Object, RedisToken>build()
      .when(SafeString.class)
        .then(RedisToken::string)
      .when(IntNum.class)
        .then(i -> RedisToken.integer(i.ival))
      .when(GVector.class)
        .then(this::toArray)
      .when(RedisToken.class)
        .then(identity())
      .otherwise()
        .then(fail(IllegalArgumentException::new))
      .apply(eval);
  }

  private RedisToken toArray(GVector<?> vector) {
    List<RedisToken> tokens = new ArrayList<>();
    for (Object object : vector) {
      tokens.add(convert(object));
    }
    return RedisToken.array(tokens);
  }

  private static RedisLibrary createLibrary(Request request) {
    return new RedisLibrary(request.getServerContext(), request.getSession());
  }
}
