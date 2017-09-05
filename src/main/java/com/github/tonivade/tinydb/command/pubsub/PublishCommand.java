/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.integer;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;

import java.util.Set;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.command.TinyDBCommand;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;

@Command("publish")
@ParamLength(2)
public class PublishCommand implements TinyDBCommand {

  private static final SafeString MESSAGE = safeString("message");

  private static final String SUBSCRIPTIONS_PREFIX = "subscriptions:";

  @Override
  public RedisToken execute(Database db, Request request) {
    Database admin = getAdminDatabase(request.getServerContext());
    DatabaseValue value = getSubscriptors(admin, request.getParam(0));

    Set<SafeString> subscribers = value.<Set<SafeString>>getValue();
    for (SafeString subscriber : subscribers) {
      publish(request, subscriber);
    }

    return integer(subscribers.size());
  }

  private void publish(Request request, SafeString subscriber) {
    getTinyDB(request.getServerContext()).publish(subscriber.toString(), message(request));
  }

  private DatabaseValue getSubscriptors(Database admin, SafeString channel) {
    DatabaseKey subscriptorsKey = safeKey(safeString(SUBSCRIPTIONS_PREFIX + channel));
    return admin.getOrDefault(subscriptorsKey, DatabaseValue.EMPTY_SET);
  }

  private RedisToken message(Request request) {
    return array(string(MESSAGE), string(request.getParam(0)), string(request.getParam(1)));
  }

}
