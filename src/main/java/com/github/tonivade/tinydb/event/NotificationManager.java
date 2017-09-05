package com.github.tonivade.tinydb.event;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.util.stream.Collectors.toSet;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
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
    return findSubscriptions(admin).filter(applyTo(event)).collect(toSet());
  }

  private Predicate<? super Entry<String, Set<SafeString>>> applyTo(Event event) {
    return entry -> event.applyTo(entry.getKey());
  }

  private Stream<Entry<String, Set<SafeString>>> findSubscriptions(Database admin) {
    return admin.entrySet().stream().filter(this::isSubscription).map(this::toEntry);
  }

  private SimpleEntry<String, Set<SafeString>> toEntry(Entry<DatabaseKey, DatabaseValue> entry) {
    return new AbstractMap.SimpleEntry<>(toPattern(entry.getKey()), 
        entry.getValue().<Set<SafeString>>getValue());
  }

  private String toPattern(DatabaseKey key) {
    SafeString value = key.getValue();
    return value.substring(PSUBSCRIPTIONS_PREFIX.length());
  }

  private boolean isSubscription(Entry<DatabaseKey, DatabaseValue> entry) {
    return entry.getKey().getValue().toString().startsWith(PSUBSCRIPTIONS_PREFIX);
  }

  private void publish(Event event) {
    for (Entry<String, Set<SafeString>> entry : getSubscriptors(server.getAdminDatabase(), event)) {
      publish(entry.getKey(), entry.getValue(), event);
    }
  }

  private void publish(String pattern, Set<SafeString> clients, Event event) {
    clients.forEach(client -> server.publish(client.toString(), toMessage(pattern, event)));
  }

  private RedisToken toMessage(String pattern, Event event) {
    return array(string(PMESSAGE), string(pattern), string(event.getChannel()), string(event.getValue()));
  }
}
