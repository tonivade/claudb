/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.pubsub;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.TinyDBServerContext;
import com.github.tonivade.tinydb.data.Database;

import io.vavr.collection.Set;

public interface BaseSubscriptionSupport
{
  default void addSubscription(String suffix, Database admin, String sessionId, SafeString channel) {
    admin.merge(safeKey(suffix + channel), set(safeString(sessionId)),
        (oldValue, newValue) -> set(oldValue.getSet().addAll(newValue.getSet())));
  }
  
  default void removeSubscription(String suffix, Database admin, String sessionId, SafeString channel) {
      admin.merge(safeKey(suffix + channel), set(safeString(sessionId)),
        (oldValue, newValue) -> set(oldValue.getSet().removeAll(newValue.getSet())));
  }
  
  default int publish(TinyDBServerContext server, Set<SafeString> clients, RedisToken message) {
    clients.forEach(client -> server.publish(client.toString(), message));
    return clients.size();
  }
}
