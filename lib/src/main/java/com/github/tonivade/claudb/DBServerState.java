/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.EMPTY_HASH;
import static com.github.tonivade.claudb.data.DatabaseValue.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseFactory;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.resp.protocol.SafeString;

public class DBServerState {

  private static final DatabaseKey SCRIPTS_KEY = safeKey("scripts");

  private final List<Database> databases = new ArrayList<>();
  private final Database admin;
  private final DatabaseFactory factory;

  public DBServerState(DatabaseFactory factory, int numDatabases) {
    this.factory = factory;
    this.admin = factory.create("admin");
    for (int i = 0; i < numDatabases; i++) {
      this.databases.add(factory.create("db-" + i));
    }
  }

  public Database getAdminDatabase() {
    return admin;
  }

  public Database getDatabase(int id) {
    return databases.get(id);
  }

  public void clear() {
    databases.clear();
    factory.clear();
  }

  public void saveScript(SafeString sha1, SafeString script) {
    DatabaseValue value = hash(entry(sha1, script));
    admin.merge(SCRIPTS_KEY, value, (oldValue, newValue) -> {
      Map<SafeString, SafeString> merge = new HashMap<>();
      merge.putAll(oldValue.getHash());
      merge.putAll(newValue.getHash());
      return hash(merge);
    });
  }

  public Optional<SafeString> getScript(SafeString sha1) {
    DatabaseValue value = admin.getOrDefault(SCRIPTS_KEY, EMPTY_HASH);
    return Optional.ofNullable(value.getHash().get(sha1));
  }

  public void cleanScripts() {
    admin.remove(SCRIPTS_KEY);
  }

  public void evictExpired(Instant now) {
    for (Database database : databases) {
      database.evictableKeys(now).forEach(database::remove);
    }
  }
}
