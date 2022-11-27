/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.scripting;

import static com.github.tonivade.purefun.Function1.identity;
import static com.github.tonivade.purefun.Precondition.checkNonNull;
import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.nullString;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.github.tonivade.purefun.Pattern1;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

import gnu.lists.FVector;
import gnu.lists.GVector;
import gnu.math.IntNum;

class SchemeInterpreter implements Interpreter {

  private final SchemeRedisBinding binding;

  protected SchemeInterpreter(SchemeRedisBinding binding) {
    this.binding = checkNonNull(binding);
  }

  static SchemeInterpreter buildFor(Request request) {
    return new SchemeInterpreter(new SchemeRedisBinding(createLibrary(request)));
  }

  @Override
  public RedisToken execute(SafeString script, List<SafeString> keys, List<SafeString> params) {
    try {
      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("scheme");
      engine.put("call-redis", binding.call());
      engine.put("pcall-redis", binding.pcall());
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
    return Pattern1.<Object, RedisToken>build()
      .when(SafeString.class)
        .then(RedisToken::string)
      .when(IntNum.class)
        .then(i -> RedisToken.integer(i.ival))
      .when(Boolean.class)
        .then(b -> Boolean.TRUE.equals(b) ? integer(1) : nullString())
      .when(GVector.class)
        .then(this::toArray)
      .when(RedisToken.class)
        .then(identity())
      .otherwise()
        .then(x -> {
          throw new IllegalArgumentException(x.getClass() + "=" + x);
        })
      .apply(eval);
  }

  private RedisToken toArray(GVector<?> vector) {
    List<RedisToken> tokens = new ArrayList<>();
    for (Object object : vector) {
      tokens.add(convert(object));
    }
    return array(tokens);
  }

  private static RedisLibrary createLibrary(Request request) {
    return new RedisLibrary(request.getServerContext(), request.getSession());
  }
}
