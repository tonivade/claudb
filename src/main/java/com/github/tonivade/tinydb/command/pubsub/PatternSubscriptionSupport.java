/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.TinyDBServerContext;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.glob.GlobPattern;

public interface PatternSubscriptionSupport extends BaseSubscriptionSupport {
  String PSUBSCRIPTION_PREFIX = "psubscription:";
  String PMESSAGE = "pmessage";

  default void addPatternSubscription(Database admin, String sessionId, SafeString channel) {
    addSubscription(PSUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }

  default void removePatternSubscription(Database admin, String sessionId, SafeString channel) {
    removeSubscription(PSUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }
  
  default Set<Entry<String, Set<SafeString>>> getPatternSubscriptions(Database admin, String channel) {
    return getPatternSubscriptions(admin).entrySet().stream()
        .filter(subscriptionApplyTo(channel))
        .collect(toSet());
  }
  
  default Map<String, Set<SafeString>> getPatternSubscriptions(Database admin) {
    return admin.entrySet().stream()
        .filter(PatternSubscriptionSupport::isPatternSubscription)
        .map(PatternSubscriptionSupport::toPatternEntry)
        .collect(toMap(Entry::getKey, Entry::getValue));
  }

  default int patternPublish(TinyDBServerContext server, String channel, SafeString message) {
    int count = 0;
    for (Entry<String, Set<SafeString>> entry : getPatternSubscriptions(server.getAdminDatabase(), channel)) {
      count += publish(server, entry.getValue(), toPatternMessage(entry.getKey(), channel, message));
    }
    return count;
  }

  static Entry<String, Set<SafeString>> toPatternEntry(Entry<DatabaseKey, DatabaseValue> entry) {
    return entry(toPattern(entry.getKey()), toSubscriptions(entry.getValue()));
  }

  static Entry<String, Set<SafeString>> entry(String pattern, Set<SafeString> subscriptions) {
    return new AbstractMap.SimpleEntry<>(pattern, subscriptions);
  }

  static Set<SafeString> toSubscriptions(DatabaseValue value) {
    return value.<Set<SafeString>>getValue();
  }

  static String toPattern(DatabaseKey key) {
    return key.getValue().substring(PSUBSCRIPTION_PREFIX.length());
  }

  static boolean isPatternSubscription(Entry<DatabaseKey, DatabaseValue> entry) {
    return entry.getKey().getValue().toString().startsWith(PSUBSCRIPTION_PREFIX);
  }

  static RedisToken toPatternMessage(String pattern, String channel, SafeString message) {
    return array(string(PMESSAGE), string(pattern), string(channel), string(message));
  }

  static Predicate<Entry<String, Set<SafeString>>> subscriptionApplyTo(String channel) {
    return entry -> new GlobPattern(entry.getKey()).match(channel);
  }
}
