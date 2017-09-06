package com.github.tonivade.tinydb.event;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.stream.Collectors.toSet;

import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.TinyDBServerContext;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;

public class NotificationManager {
  
  private static final String PMESSAGE = "PMESSAGE";
  private static final String PSUBSCRIPTIONS_PREFIX = "psubscriptions:";

  private final TinyDBServerContext server;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  
  public NotificationManager(TinyDBServerContext server) {
    this.server = server;
  }
  
  public void enqueue(Event event) {
    executor.execute(() -> publish(event));
  }
  
  private Set<Entry<String, Set<SafeString>>> getSubscriptors(Database admin, Event event) {
    // Pattern -> Subscriptions
    return findAllSubscriptions(admin).filter(subscriptionApplyTo(event)).collect(toSet());
  }

  private Predicate<? super Entry<String, Set<SafeString>>> subscriptionApplyTo(Event event) {
    return entry -> event.applyTo(entry.getKey());
  }

  private Stream<Entry<String, Set<SafeString>>> findAllSubscriptions(Database admin) {
    return admin.entrySet().stream().filter(this::isSubscription).map(this::toEntry);
  }

  private Entry<String, Set<SafeString>> toEntry(Entry<DatabaseKey, DatabaseValue> entry) {
    return entry(toPattern(entry.getKey()), toSubscriptions(entry.getValue()));
  }

  private Entry<String, Set<SafeString>> entry(String pattern, Set<SafeString> subscriptions) {
    return new AbstractMap.SimpleEntry<>(pattern, subscriptions);
  }

  private Set<SafeString> toSubscriptions(DatabaseValue value) {
    return value.<Set<SafeString>>getValue();
  }

  private String toPattern(DatabaseKey key) {
    return key.getValue().substring(PSUBSCRIPTIONS_PREFIX.length());
  }

  private boolean isSubscription(Entry<DatabaseKey, DatabaseValue> entry) {
    return entry.getKey().getValue().toString().startsWith(PSUBSCRIPTIONS_PREFIX);
  }

  private void publish(Event event) {
    for (Entry<String, Set<SafeString>> entry : getSubscriptors(server.getAdminDatabase(), event)) {
      publishAll(entry.getValue(), toMessage(entry.getKey(), event));
    }
  }

  private void publishAll(Set<SafeString> clients, RedisToken message) {
    clients.forEach(client -> server.publish(client.toString(), message));
  }

  private RedisToken toMessage(String pattern, Event event) {
    return array(string(PMESSAGE), string(pattern), string(event.getChannel()), string(event.getValue()));
  }
}
