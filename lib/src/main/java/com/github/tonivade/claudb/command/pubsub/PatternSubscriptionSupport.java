/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.stream.Collectors.toSet;
import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.claudb.glob.GlobPattern;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface PatternSubscriptionSupport extends BaseSubscriptionSupport {
  String PSUBSCRIPTION_PREFIX = "psubscription:";
  String PMESSAGE = "pmessage";

  default void addPatternSubscription(Database admin, String sessionId, SafeString channel) {
    addSubscription(PSUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }

  default void removePatternSubscription(Database admin, String sessionId, SafeString channel) {
    removeSubscription(PSUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }

  default Set<Map.Entry<String, Set<SafeString>>> getPatternSubscriptions(Database admin, String channel) {
    return getPatternSubscriptions(admin).entrySet().stream().filter(subscriptionApplyTo(channel)).collect(toSet());
  }

  default Map<String, Set<SafeString>> getPatternSubscriptions(Database admin) {
    return admin.entrySet().stream()
        .filter(PatternSubscriptionSupport::isPatternSubscription)
        .map(PatternSubscriptionSupport::toPatternEntry)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  default int patternPublish(DBServerContext server, String channel, SafeString message) {
    int count = 0;
    for (Map.Entry<String, Set<SafeString>> entry : getPatternSubscriptions(server.getAdminDatabase(), channel)) {
      count += publish(server, entry.getValue(), toPatternMessage(entry.getKey(), channel, message));
    }
    return count;
  }

  static Map.Entry<String, Set<SafeString>> toPatternEntry(Map.Entry<DatabaseKey, DatabaseValue> entry) {
    return new AbstractMap.SimpleEntry<>(PatternSubscriptionSupport.toPattern(entry.getKey()), entry.getValue().getSet());
  }

  static String toPattern(DatabaseKey key) {
    return key.getValue().substring(PSUBSCRIPTION_PREFIX.length());
  }

  static boolean isPatternSubscription(Map.Entry<DatabaseKey, DatabaseValue> entry) {
    return entry.getKey().getValue().toString().startsWith(PSUBSCRIPTION_PREFIX);
  }

  static RedisToken toPatternMessage(String pattern, String channel, SafeString message) {
    return array(string(PMESSAGE), string(pattern), string(channel), string(message));
  }

  static Predicate<Map.Entry<String, Set<SafeString>>> subscriptionApplyTo(String channel) {
    return entry -> new GlobPattern(entry.getKey()).match(channel);
  }
}
