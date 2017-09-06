/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.pubsub;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.command.annotation.PubSubAllowed;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.Database;

@ReadOnly
@Command("punsubscribe")
@ParamLength(1)
@PubSubAllowed
public class PatternUnsubscribeCommand implements TinyDBCommand {

  private static final String PUNSUBSCRIBE = "punsubscribe";
  private static final String PSUBSCRIPTION_PREFIX = "psubscription:";

  @Override
  public RedisToken execute(Database db, Request request) {
    Database admin = getAdminDatabase(request.getServerContext());
    Collection<SafeString> channels = getChannels(request);
    int i = channels.size();
    List<Object> result = new LinkedList<>();
    for (SafeString channel : channels) {
      admin.merge(safeKey(PSUBSCRIPTION_PREFIX + channel), set(safeString(request.getSession().getId())),
          (oldValue, newValue) -> {
            Set<SafeString> merge = new HashSet<>();
            merge.addAll(oldValue.getValue());
            merge.remove(safeString(request.getSession().getId()));
            return set(merge);
          });
      getSessionState(request.getSession()).removeSubscription(channel);
      result.addAll(asList(PUNSUBSCRIBE, channel, --i));
    }
    return convert(result);
  }

  private Collection<SafeString> getChannels(Request request) {
    if (request.getParams().isEmpty()) {
      return getSessionState(request.getSession()).getSubscriptions();
    }
    return request.getParams();
  }

}
