/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb.command.pubsub;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.AbstractMap;
import java.util.HashSet;
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
import com.github.tonivade.tinydb.event.Event;
import com.github.tonivade.tinydb.glob.GlobPattern;

abstract class SubscriptionManager {
  
  private static final String SUBSCRIPTION_PREFIX = "subscription:";
  private static final String PSUBSCRIPTION_PREFIX = "psubscription:";
  private static final String MESSAGE = "message";
  private static final String PMESSAGE = "pmessage";

  public void addSubscription(Database admin, String sessionId, SafeString channel) {
    addSubscription(SUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }

  public void removeSubscription(Database admin, String sessionId, SafeString channel) {
    removeSubscription(SUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }
  
  public Set<SafeString> getSubscription(Database admin, String channel) {
    return getSubscriptions(admin).getOrDefault(channel, emptySet());
  }
  
  public Map<String, Set<SafeString>> getSubscriptions(Database admin) {
    return admin.entrySet().stream()
        .filter(this::isSubscription)
        .map(this::toEntry)
        .collect(toMap(Entry::getKey, Entry::getValue));
  }

  public void addPatternSubscription(Database admin, String sessionId, SafeString channel) {
    addSubscription(PSUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }

  public void removePatternSubscription(Database admin, String sessionId, SafeString channel) {
    removeSubscription(PSUBSCRIPTION_PREFIX, admin, sessionId, channel);
  }
  
  public Set<Entry<String, Set<SafeString>>> getPatternSubscriptions(Database admin, String channel) {
    return getPatternSubscriptions(admin).entrySet().stream()
        .filter(subscriptionApplyTo(channel))
        .collect(toSet());
  }
  
  public Map<String, Set<SafeString>> getPatternSubscriptions(Database admin) {
    return admin.entrySet().stream()
        .filter(this::isPatternSubscription)
        .map(this::toPatternEntry)
        .collect(toMap(Entry::getKey, Entry::getValue));
  }

  public void publish(TinyDBServerContext server, Event event) {
    for (Entry<String, Set<SafeString>> entry : getPatternSubscriptions(server.getAdminDatabase(), event.getChannel())) {
      publish(server, entry.getValue(), toPatternMessage(entry.getKey(), event));
    }
  }

  public void publish(TinyDBServerContext server, String channel, SafeString message) {
    for (SafeString subscriber : getSubscription(server.getAdminDatabase(), channel)) {
      publish(server, channel, message, subscriber);
    }
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

  private Entry<String, Set<SafeString>> toEntry(Entry<DatabaseKey, DatabaseValue> entry) {
    return entry(toChannel(entry.getKey()), toSubscriptions(entry.getValue()));
  }

  private Entry<String, Set<SafeString>> toPatternEntry(Entry<DatabaseKey, DatabaseValue> entry) {
    return entry(toPattern(entry.getKey()), toSubscriptions(entry.getValue()));
  }

  private Entry<String, Set<SafeString>> entry(String pattern, Set<SafeString> subscriptions) {
    return new AbstractMap.SimpleEntry<>(pattern, subscriptions);
  }

  private Set<SafeString> toSubscriptions(DatabaseValue value) {
    return value.<Set<SafeString>>getValue();
  }

  private String toPattern(DatabaseKey key) {
    return key.getValue().substring(PSUBSCRIPTION_PREFIX.length());
  }

  private String toChannel(DatabaseKey key) {
    return key.getValue().substring(SUBSCRIPTION_PREFIX.length());
  }

  private boolean isPatternSubscription(Entry<DatabaseKey, DatabaseValue> entry) {
    return entry.getKey().getValue().toString().startsWith(PSUBSCRIPTION_PREFIX);
  }

  private boolean isSubscription(Entry<DatabaseKey, DatabaseValue> entry) {
    return entry.getKey().getValue().toString().startsWith(SUBSCRIPTION_PREFIX);
  }

  private void publish(TinyDBServerContext server, Set<SafeString> clients, RedisToken message) {
    clients.forEach(client -> server.publish(client.toString(), message));
  }

  private RedisToken toPatternMessage(String pattern, Event event) {
    return array(string(PMESSAGE), string(pattern), string(event.getChannel()), string(event.getValue()));
  }

  private void publish(TinyDBServerContext server, String channel, SafeString message, SafeString subscriber) {
    server.publish(subscriber.toString(), toMessage(channel, message));
  }

  private RedisToken toMessage(String channel, SafeString message) {
    return array(string(MESSAGE), string(channel), string(message));
  }

  private Predicate<Entry<String, Set<SafeString>>> subscriptionApplyTo(String channel) {
    return entry -> new GlobPattern(entry.getKey()).match(channel);
  }
}
