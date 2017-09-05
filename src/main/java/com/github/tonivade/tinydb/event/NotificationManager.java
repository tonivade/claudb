package com.github.tonivade.tinydb.event;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.TinyDBServerContext;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;

public class NotificationManager {
  
  private static final String SUBSCRIPTIONS_PREFIX = "notifications:";

  private final TinyDBServerContext server;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  
  public NotificationManager(TinyDBServerContext server) {
    this.server = server;
  }
  
  public void enqueue(Event event) {
    executor.execute(() -> publish(event));
  }
  
  private DatabaseValue getSubscriptors(Database admin, SafeString channel) {
    DatabaseKey subscriptorsKey = safeKey(safeString(SUBSCRIPTIONS_PREFIX + channel));
    return admin.getOrDefault(subscriptorsKey, DatabaseValue.EMPTY_SET);
  }

  private void publish(Event event) {
    DatabaseValue value = getSubscriptors(server.getAdminDatabase(), event.toChannel());
    Set<SafeString> subscribers = value.<Set<SafeString>>getValue();
    for (SafeString safeString : subscribers) {
      
    }
  }
}
