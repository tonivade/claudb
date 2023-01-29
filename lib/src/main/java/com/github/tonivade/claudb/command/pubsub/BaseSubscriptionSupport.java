/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command.pubsub;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.set;
import static com.github.tonivade.resp.protocol.SafeString.safeString;

import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.HashSet;
import java.util.Set;

public interface BaseSubscriptionSupport {

  default void addSubscription(String suffix, Database admin, String sessionId, SafeString channel) {
    admin.merge(safeKey(suffix + channel), set(safeString(sessionId)),
        (oldValue, newValue) -> {
          Set<SafeString> merge = new HashSet<>();
          merge.addAll(oldValue.getSet());
          merge.addAll(newValue.getSet());
          return set(merge);
        });
  }

  default void removeSubscription(String suffix, Database admin, String sessionId, SafeString channel) {
      admin.merge(safeKey(suffix + channel), set(safeString(sessionId)),
        (oldValue, newValue) -> {
          Set<SafeString> merge = new HashSet<>();
          merge.addAll(oldValue.getSet());
          merge.removeAll(newValue.getSet());
          return set(merge);
        });
  }

  default int publish(DBServerContext server, Set<SafeString> clients, RedisToken message) {
    clients.forEach(client -> server.publish(client.toString(), message));
    return clients.size();
  }
}
