/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.event;

import static com.github.tonivade.resp.util.Precondition.checkNonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.command.pubsub.PatternSubscriptionSupport;

public class NotificationManager implements PatternSubscriptionSupport {

  private final DBServerContext server;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  public NotificationManager(DBServerContext server) {
    this.server = checkNonNull(server);
  }

  public void start() {
    // nothing to do
  }

  public void stop() {
    executor.shutdown();
  }

  public void enqueue(Event event) {
    executor.execute(() -> patternPublish(server, event.getChannel(), event.getValue()));
  }
}
