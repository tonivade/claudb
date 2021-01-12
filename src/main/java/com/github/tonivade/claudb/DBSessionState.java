/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import java.util.HashSet;
import java.util.Set;

import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.resp.protocol.SafeString;

public class DBSessionState {

  private int db;

  private final Set<SafeString> subscriptions = new HashSet<>();

  public int getCurrentDB() {
    return db;
  }

  public void setCurrentDB(int db) {
    this.db = db;
  }

  public Sequence<SafeString> getSubscriptions() {
    return ImmutableSet.from(subscriptions);
  }

  public void addSubscription(SafeString channel) {
    subscriptions.add(channel);
  }

  public void removeSubscription(SafeString channel) {
    subscriptions.remove(channel);
  }

  public boolean isSubscribed() {
    return !subscriptions.isEmpty();
  }
}
