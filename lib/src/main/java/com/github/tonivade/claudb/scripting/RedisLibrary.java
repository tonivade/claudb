/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.scripting;

import static com.github.tonivade.purefun.Precondition.checkNonNull;
import static com.github.tonivade.purefun.data.Sequence.arrayOf;

import com.github.tonivade.resp.command.DefaultRequest;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.command.ServerContext;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

public class RedisLibrary {

  private final ServerContext context;
  private final Session session;

  public RedisLibrary(ServerContext context, Session session) {
    this.context = checkNonNull(context);
    this.session = checkNonNull(session);
  }

  public RedisToken call(SafeString commandName, SafeString... params) {
    return getCommand(commandName).execute(createRequest(commandName, params));
  }

  public RedisToken pcall(SafeString commandName, SafeString... params) {
    try {
      return call(commandName, params);
    } catch (Exception e) {
      return RedisToken.error(e.getMessage());
    }
  }

  private RespCommand getCommand(SafeString commandName) {
    return context.getCommand(commandName.toString());
  }

  private Request createRequest(SafeString commandName, SafeString... params) {
    return new DefaultRequest(context, session, commandName, arrayOf(params));
  }
}