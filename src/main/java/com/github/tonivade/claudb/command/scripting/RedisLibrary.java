/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.scripting;

import static com.github.tonivade.purefun.data.Sequence.arrayOf;
import static java.util.Objects.requireNonNull;

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
    this.context = requireNonNull(context);
    this.session = requireNonNull(session);
  }

  public RedisToken call(SafeString commandName, SafeString... params) {
    return getCommand(commandName).execute(createRequest(commandName, params));
  }

  private RespCommand getCommand(SafeString commandName) {
    return context.getCommand(commandName.toString());
  }

  private Request createRequest(SafeString commandName, SafeString... params) {
    return new DefaultRequest(context, session, commandName, arrayOf(params));
  }
}
