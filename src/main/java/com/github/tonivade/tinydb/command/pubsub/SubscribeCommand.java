/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.pubsub;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;
import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.ITinyDBCommand;

import com.github.tonivade.tinydb.command.annotation.PubSubAllowed;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.IDatabase;

@ReadOnly
@Command("subscribe")
@ParamLength(1)
@PubSubAllowed
public class SubscribeCommand implements ITinyDBCommand {

  private static final SafeString SUBSCRIBE = safeString("subscribe");

  private static final String SUBSCRIPTIONS_PREFIX = "subscriptions:";

  @Override
  public RedisToken execute(IDatabase db, IRequest request) {
    IDatabase admin = getAdminDatabase(request.getServerContext());
    int i = 1;
    List<Object> result = new LinkedList<>();
    for (SafeString channel : request.getParams()) {
      admin.merge(safeKey(safeString(SUBSCRIPTIONS_PREFIX + channel)), set(safeString(request.getSession().getId())),
          (oldValue, newValue) -> {
            Set<SafeString> merge = new HashSet<>();
            merge.addAll(oldValue.getValue());
            merge.add(safeString(request.getSession().getId()));
            return set(merge);
          });
      getSessionState(request.getSession()).addSubscription(channel);
      result.addAll(asList(SUBSCRIBE, channel, i++));
    }
    return convert(result);
  }

}
