/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.pubsub;

import static java.util.Arrays.asList;

import java.util.LinkedList;
import java.util.List;

import com.github.tonivade.claudb.command.DBCommand;
import com.github.tonivade.claudb.command.annotation.PubSubAllowed;
import com.github.tonivade.claudb.command.annotation.ReadOnly;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

@ReadOnly
@Command("subscribe")
@ParamLength(1)
@PubSubAllowed
public class SubscribeCommand implements DBCommand, SubscriptionSupport {

  private static final String SUBSCRIBE = "subscribe";

  @Override
  public RedisToken execute(Database db, Request request) {
    Database admin = getAdminDatabase(request.getServerContext());
    String sessionId = getSessionId(request);
    Sequence<SafeString> channels = getChannels(request);
    int i = channels.size();
    List<Object> result = new LinkedList<>();
    for (SafeString channel : request.getParams()) {
      addSubscription(admin, sessionId, channel);
      getSessionState(request.getSession()).addSubscription(channel);
      result.addAll(asList(SUBSCRIBE, channel, ++i));
    }
    return convert(result);
  }

  private String getSessionId(Request request) {
    return request.getSession().getId();
  }

  private Sequence<SafeString> getChannels(Request request) {
    return getSessionState(request.getSession()).getSubscriptions();
  }
}
