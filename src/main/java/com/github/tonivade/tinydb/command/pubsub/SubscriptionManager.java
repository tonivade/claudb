package com.github.tonivade.tinydb.command.pubsub;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;

import java.util.HashSet;
import java.util.Set;

import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.data.Database;

class SubscriptionManager {
  
  private static final String SUBSCRIPTION_PREFIX = "subscription:";
  private static final String PSUBSCRIPTION_PREFIX = "psubscription:";

  void addSubscription(Database admin, String sessionId, SafeString channel) {
    addSubscription(SUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }

  void removeSubscription(Database admin, String sessionId, SafeString channel) {
    removeSubscription(SUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }

  void addPatternSubscription(Database admin, String sessionId, SafeString channel) {
    addSubscription(PSUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }

  void removePatternSubscription(Database admin, String sessionId, SafeString channel) {
    removeSubscription(PSUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }
  
  private void addSubscription(String suffix, Database admin, String sessionId, SafeString channel) {
    admin.merge(safeKey(suffix + channel), set(safeString(sessionId)),
        (oldValue, newValue) -> {
          Set<SafeString> merge = new HashSet<>();
          merge.addAll(oldValue.getValue());
          merge.add(safeString(sessionId));
          return set(merge);
        });
  }
  
  private void removeSubscription(String suffix, Database admin, String sessionId, SafeString channel) {
      admin.merge(safeKey(suffix + channel), set(safeString(sessionId)),
          (oldValue, newValue) -> {
            Set<SafeString> merge = new HashSet<>();
            merge.addAll(oldValue.getValue());
            merge.remove(safeString(sessionId));
            return set(merge);
          });
  }
}
