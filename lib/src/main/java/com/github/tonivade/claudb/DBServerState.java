/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.claudb.data.DatabaseValue.EMPTY_HASH;
import static com.github.tonivade.claudb.data.DatabaseValue.entry;
import static com.github.tonivade.claudb.data.DatabaseValue.hash;
import static com.github.tonivade.claudb.data.DatabaseValue.set;
import static com.github.tonivade.resp.protocol.SafeString.safeString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseFactory;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.claudb.persistence.RDBInputStream;
import com.github.tonivade.claudb.persistence.RDBOutputStream;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

public class DBServerState {

  private static final SafeString SLAVES = safeString("slaves");
  private static final DatabaseKey SLAVES_KEY = safeKey("slaves");
  private static final DatabaseKey SCRIPTS_KEY = safeKey("scripts");

  private boolean master = true;

  private final List<Database> databases = new ArrayList<>();
  private final Database admin;
  private final DatabaseFactory factory;

  private final Queue<RedisToken> queue = new LinkedList<>();

  public DBServerState(DatabaseFactory factory, int numDatabases) {
    this.factory = factory;
    this.admin = factory.create("admin");
    for (int i = 0; i < numDatabases; i++) {
      this.databases.add(factory.create("db-" + i));
    }
  }

  public void append(RedisToken command) {
    queue.offer(command);
  }

  public void setMaster(boolean master) {
    this.master = master;
  }

  public boolean isMaster() {
    return master;
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

  public boolean hasSlaves() {
    return !admin.getSet(SLAVES).isEmpty();
  }

  public void exportRDB(OutputStream output) throws IOException {
    new RDBOutputStream(output).write(databases);
  }

  public void importRDB(InputStream input) throws IOException {
    RDBInputStream rdb = new RDBInputStream(input);

    Map<Integer, Map<DatabaseKey, DatabaseValue>> load = rdb.parse();
    for (Map.Entry<Integer, Map<DatabaseKey, DatabaseValue>> entry : load.entrySet()) {
      databases.get(entry.getKey()).overrideAll(entry.getValue());
    }
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

  public Set<SafeString> getSlaves() {
    return getAdminDatabase().getSet(SLAVES);
  }

  public void addSlave(String id) {
    getAdminDatabase().merge(SLAVES_KEY, set(safeString(id)),
            (oldValue, newValue) -> {
              Set<SafeString> merge = new HashSet<>();
              merge.addAll(oldValue.getSet());
              merge.addAll(newValue.getSet());
              return set(merge);
            });
  }

  public void removeSlave(String id) {
    getAdminDatabase().merge(SLAVES_KEY, set(safeString(id)),
            (oldValue, newValue) -> {
              Set<SafeString> merge = new HashSet<>();
              merge.addAll(oldValue.getSet());
              merge.removeAll(newValue.getSet());
              return set(merge);
            });
  }

  public List<RedisToken> getCommandsToReplicate() {
    List<RedisToken> list = new ArrayList<>(queue);
    queue.clear();
    return list;
  }

  public void evictExpired(Instant now) {
    for (Database database : databases) {
      database.evictableKeys(now).forEach(database::remove);
    }
  }
}
