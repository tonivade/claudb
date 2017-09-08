/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toMap;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.TinyDBServerContext;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;

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
    return getSubscriptions(admin).getOrDefault(channel, emptySet());
  }
  
  default Map<String, Set<SafeString>> getSubscriptions(Database admin) {
    return admin.entrySet().stream()
        .filter(SubscriptionSupport::isSubscription)
        .map(SubscriptionSupport::toEntry)
        .collect(toMap(Entry::getKey, Entry::getValue));
  }

  default int publish(TinyDBServerContext server, String channel, SafeString message) {
    return publish(server, getSubscription(server.getAdminDatabase(), channel), toMessage(channel, message));
  }
  
  static Entry<String, Set<SafeString>> toEntry(Entry<DatabaseKey, DatabaseValue> entry) {
    return entry(toChannel(entry.getKey()), toSubscriptions(entry.getValue()));
  }

  static Entry<String, Set<SafeString>> entry(String pattern, Set<SafeString> subscriptions) {
    return new AbstractMap.SimpleEntry<>(pattern, subscriptions);
  }

  static Set<SafeString> toSubscriptions(DatabaseValue value) {
    return value.<Set<SafeString>>getValue();
  }

  static String toChannel(DatabaseKey key) {
    return key.getValue().substring(SUBSCRIPTION_PREFIX.length());
  }

  static boolean isSubscription(Entry<DatabaseKey, DatabaseValue> entry) {
    return entry.getKey().getValue().toString().startsWith(SUBSCRIPTION_PREFIX);
  }

  static RedisToken toMessage(String channel, SafeString message) {
    return array(string(MESSAGE), string(channel), string(message));
  }
}
