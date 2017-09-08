/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.pubsub;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;

import java.util.HashSet;
import java.util.Set;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.TinyDBServerContext;
import com.github.tonivade.tinydb.data.Database;

public interface BaseSubscriptionSupport
{
  default void addSubscription(String suffix, Database admin, String sessionId, SafeString channel) {
    admin.merge(safeKey(suffix + channel), set(safeString(sessionId)),
        (oldValue, newValue) -> {
          Set<SafeString> merge = new HashSet<>();
          merge.addAll(oldValue.getValue());
          merge.add(safeString(sessionId));
          return set(merge);
        });
  }
  
  default void removeSubscription(String suffix, Database admin, String sessionId, SafeString channel) {
      admin.merge(safeKey(suffix + channel), set(safeString(sessionId)),
          (oldValue, newValue) -> {
            Set<SafeString> merge = new HashSet<>();
            merge.addAll(oldValue.getValue());
            merge.remove(safeString(sessionId));
            return set(merge);
          });
  }
  
  default int publish(TinyDBServerContext server, Set<SafeString> clients, RedisToken message) {
    clients.forEach(client -> server.publish(client.toString(), message));
    return clients.size();
  }
}
