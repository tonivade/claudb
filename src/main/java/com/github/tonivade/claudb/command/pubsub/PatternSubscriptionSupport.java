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
import com.github.tonivade.claudb.glob.GlobPattern;
import com.github.tonivade.purefun.Matcher1;
import com.github.tonivade.purefun.Tuple2;
import com.github.tonivade.purefun.data.ImmutableMap;
import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

public interface PatternSubscriptionSupport extends BaseSubscriptionSupport {
  String PSUBSCRIPTION_PREFIX = "psubscription:";
  String PMESSAGE = "pmessage";

  default void addPatternSubscription(Database admin, String sessionId, SafeString channel) {
    addSubscription(PSUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }

  default void removePatternSubscription(Database admin, String sessionId, SafeString channel) {
    removeSubscription(PSUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }

  default ImmutableSet<Tuple2<String, ImmutableSet<SafeString>>> getPatternSubscriptions(Database admin, String channel) {
    return getPatternSubscriptions(admin).entries().filter(subscriptionApplyTo(channel));
  }

  default ImmutableMap<String, ImmutableSet<SafeString>> getPatternSubscriptions(Database admin) {
    return ImmutableMap.from(admin.entrySet()
        .filter(PatternSubscriptionSupport::isPatternSubscription)
        .map(PatternSubscriptionSupport::toPatternEntry));
  }

  default int patternPublish(DBServerContext server, String channel, SafeString message) {
    int count = 0;
    for (Tuple2<String, ImmutableSet<SafeString>> entry : getPatternSubscriptions(server.getAdminDatabase(), channel)) {
      count += publish(server, entry.get2(), toPatternMessage(entry.get1(), channel, message));
    }
    return count;
  }

  static Tuple2<String, ImmutableSet<SafeString>> toPatternEntry(Tuple2<DatabaseKey, DatabaseValue> entry) {
    return entry.map(PatternSubscriptionSupport::toPattern, DatabaseValue::getSet);
  }

  static String toPattern(DatabaseKey key) {
    return key.getValue().substring(PSUBSCRIPTION_PREFIX.length());
  }

  static boolean isPatternSubscription(Tuple2<DatabaseKey, DatabaseValue> entry) {
    return entry.get1().getValue().toString().startsWith(PSUBSCRIPTION_PREFIX);
  }

  static RedisToken toPatternMessage(String pattern, String channel, SafeString message) {
    return array(string(PMESSAGE), string(pattern), string(channel), string(message));
  }

  static Matcher1<Tuple2<String, ImmutableSet<SafeString>>> subscriptionApplyTo(String channel) {
    return entry -> new GlobPattern(entry.get1()).match(channel);
  }
}
