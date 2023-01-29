/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;

import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    return getSubscriptions(admin).getOrDefault(channel, Collections.emptySet());
  }

  default Map<String, Set<SafeString>> getSubscriptions(Database admin) {
    return admin.entrySet().stream()
        .filter(SubscriptionSupport::isSubscription)
        .map(SubscriptionSupport::toEntry)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  default int publish(DBServerContext server, String channel, SafeString message) {
    return publish(server, getSubscription(server.getAdminDatabase(), channel), toMessage(channel, message));
  }

  static Map.Entry<String, Set<SafeString>> toEntry(Map.Entry<DatabaseKey, DatabaseValue> entry) {
    return new AbstractMap.SimpleEntry<>(toChannel(entry.getKey()), entry.getValue().getSet());
  }

  static String toChannel(DatabaseKey key) {
    return key.getValue().substring(SUBSCRIPTION_PREFIX.length());
  }

  static boolean isSubscription(Map.Entry<DatabaseKey, DatabaseValue> entry) {
    return entry.getKey().getValue().toString().startsWith(SUBSCRIPTION_PREFIX);
  }

  static RedisToken toMessage(String channel, SafeString message) {
    return array(string(MESSAGE), string(channel), string(message));
  }
}
