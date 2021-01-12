/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;

import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.ImmutableMap;
import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

public interface SubscriptionSupport extends BaseSubscriptionSupport {

  String SUBSCRIPTION_PREFIX = "subscription:";
  String MESSAGE = "message";

  default void addSubscription(Database admin, String sessionId, SafeString channel) {
    addSubscription(SUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }

  default void removeSubscription(Database admin, String sessionId, SafeString channel) {
    removeSubscription(SUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }

  default ImmutableSet<SafeString> getSubscription(Database admin, String channel) {
    return getSubscriptions(admin).getOrDefault(channel, ImmutableSet::empty);
  }

  default ImmutableMap<String, ImmutableSet<SafeString>> getSubscriptions(Database admin) {
    return ImmutableMap.from(admin.entrySet()
        .filter(SubscriptionSupport::isSubscription)
        .map(SubscriptionSupport::toEntry));
  }

  default int publish(DBServerContext server, String channel, SafeString message) {
    return publish(server, getSubscription(server.getAdminDatabase(), channel), toMessage(channel, message));
  }

  static Tuple2<String, ImmutableSet<SafeString>> toEntry(Tuple2<DatabaseKey, DatabaseValue> entry) {
    return Tuple.of(toChannel(entry.get1()), entry.get2().getSet());
  }

  static String toChannel(DatabaseKey key) {
    return key.getValue().substring(SUBSCRIPTION_PREFIX.length());
  }

  static boolean isSubscription(Tuple2<DatabaseKey, DatabaseValue> entry) {
    return entry.get1().getValue().toString().startsWith(SUBSCRIPTION_PREFIX);
  }

  static RedisToken toMessage(String channel, SafeString message) {
    return array(string(MESSAGE), string(channel), string(message));
  }
}
