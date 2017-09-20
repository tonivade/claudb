/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.stream.Collectors.toMap;

import java.util.Map;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.claudb.TinyDBServerContext;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

public interface SubscriptionSupport extends BaseSubscriptionSupport {

  String SUBSCRIPTION_PREFIX = "subscription:";
  String MESSAGE = "message";

  default void addSubscription(Database admin, String sessionId, SafeString channel) {
    addSubscription(SUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }

  default void removeSubscription(Database admin, String sessionId, SafeString channel) {
    removeSubscription(SUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }
  
  default Set<SafeString> getSubscription(Database admin, String channel) {
    return getSubscriptions(admin).getOrDefault(channel, HashSet.empty());
  }
  
  default Map<String, Set<SafeString>> getSubscriptions(Database admin) {
    return admin.entrySet()
        .filter(SubscriptionSupport::isSubscription)
        .map(SubscriptionSupport::toEntry)
        .collect(toMap(Tuple2::_1, Tuple2::_2));
  }

  default int publish(TinyDBServerContext server, String channel, SafeString message) {
    return publish(server, getSubscription(server.getAdminDatabase(), channel), toMessage(channel, message));
  }
  
  static Tuple2<String, Set<SafeString>> toEntry(Tuple2<DatabaseKey, DatabaseValue> entry) {
    return Tuple.of(toChannel(entry._1()), entry._2().getSet());
  }

  static String toChannel(DatabaseKey key) {
    return key.getValue().substring(SUBSCRIPTION_PREFIX.length());
  }

  static boolean isSubscription(Tuple2<DatabaseKey, DatabaseValue> entry) {
    return entry._1().getValue().toString().startsWith(SUBSCRIPTION_PREFIX);
  }

  static RedisToken toMessage(String channel, SafeString message) {
    return array(string(MESSAGE), string(channel), string(message));
  }
}
