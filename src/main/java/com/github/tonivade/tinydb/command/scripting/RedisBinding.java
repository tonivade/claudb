/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.util.Arrays.asList;
import static javaslang.API.$;
import static javaslang.API.Case;
import static javaslang.API.Match;

import java.util.function.Predicate;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import com.github.tonivade.resp.command.ICommand;
import com.github.tonivade.resp.command.IServerContext;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.Response;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisTokenType;
import com.github.tonivade.resp.protocol.SafeString;

public class RedisBinding extends OneArgFunction {

  private IServerContext context;
  private ISession session;

  public RedisBinding(IServerContext context, ISession session) {
    this.context = context;
    this.session = session;
  }

  @Override
  public LuaValue call(LuaValue arg) {
    return convert(call(safeString(arg.tojstring())));
  }

  public RedisToken call(SafeString commandName, SafeString ... params) {
    Response response = new Response();
    getCommand(commandName).execute(createRequest(commandName, params), response);
    return response.build();
  }

  private LuaValue convert(RedisToken token) {
    return Match(token).of(Case(isType(RedisTokenType.STRING), value -> LuaString.valueOf(value.getValue().toString())),
                           Case(isType(RedisTokenType.STATUS), value -> LuaString.valueOf(value.getValue().toString())),
                           Case(isType(RedisTokenType.ARRAY), value -> LuaString.valueOf(value.toString())),
                           Case(isType(RedisTokenType.INTEGER), value -> LuaString.valueOf(value.toString())),
                           Case($(), value -> LuaString.valueOf(value.toString())));
  }

  private Predicate<? super RedisToken> isType(RedisTokenType type) {
    return value -> value.getType() == type;
  }

  private ICommand getCommand(SafeString commandName) {
    return context.getCommand(commandName.toString());
  }

  private Request createRequest(SafeString commandName, SafeString... params) {
    return new Request(context, session, commandName, asList(params));
  }
}
