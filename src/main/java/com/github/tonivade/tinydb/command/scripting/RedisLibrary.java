/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.scripting;

import static java.util.Arrays.asList;

import com.github.tonivade.resp.command.ICommand;
import com.github.tonivade.resp.command.IServerContext;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

public class RedisLibrary {

  private IServerContext context;
  private ISession session;

  public RedisLibrary(IServerContext context, ISession session) {
    this.context = context;
    this.session = session;
  }

  public RedisToken call(SafeString commandName, SafeString... params) {
    return getCommand(commandName).execute(createRequest(commandName, params));
  }

  private ICommand getCommand(SafeString commandName) {
    return context.getCommand(commandName.toString());
  }

  private Request createRequest(SafeString commandName, SafeString... params) {
    return new Request(context, session, commandName, asList(params));
  }
}
