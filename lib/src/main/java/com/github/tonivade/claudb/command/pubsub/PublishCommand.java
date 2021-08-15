/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.integer;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.data.Database;

@Command("publish")
@ParamLength(2)
public class PublishCommand implements DBCommand, SubscriptionSupport, PatternSubscriptionSupport {

  @Override
  public RedisToken execute(Database db, Request request) {
    String channel = request.getParam(0).toString();
    SafeString message = request.getParam(1);
    return integer(publishAll(getClauDB(request.getServerContext()), channel, message));
  }

  private int publishAll(DBServerContext server, String channel, SafeString message) {
    int count = publish(server, channel, message);
    int pcount = patternPublish(server, channel, message);
    return count + pcount;
  }
}
