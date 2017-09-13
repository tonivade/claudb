/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb;

import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.tinydb.data.DatabaseKey.safeKey;
import static com.github.tonivade.tinydb.data.DatabaseValue.entry;
import static com.github.tonivade.tinydb.data.DatabaseValue.hash;
import static com.github.tonivade.tinydb.data.DatabaseValue.set;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseFactory;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.DatabaseValue;
import com.github.tonivade.tinydb.persistence.RDBInputStream;
import com.github.tonivade.tinydb.persistence.RDBOutputStream;

import io.vavr.collection.Set;

public class TinyDBServerState {

  private static final int RDB_VERSION = 6;

  private static final SafeString SLAVES = safeString("slaves");
  private static final DatabaseKey SLAVES_KEY = safeKey("slaves");
  private static final DatabaseKey SCRIPTS_KEY = safeKey("scripts");

  private boolean master = true;

  private final List<Database> databases = new ArrayList<>();
  private final Database admin;
  private final DatabaseFactory factory;

  private final Queue<RedisToken> queue = new LinkedList<>();

  public TinyDBServerState(DatabaseFactory factory, int numDatabases) {
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
    RDBOutputStream rdb = new RDBOutputStream(output);
    rdb.preamble(RDB_VERSION);
    for (int i = 0; i < databases.size(); i++) {
      Database db = databases.get(i);
      if (!db.isEmpty()) {
        rdb.select(i);
        rdb.dabatase(db);
      }
    }
    rdb.end();
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
      merge.putAll(oldValue.getValue());
      merge.putAll(newValue.getValue());
      return hash(merge.entrySet());
    });
  }

  public Optional<SafeString> getScript(SafeString sha1) {
    DatabaseValue value = admin.getOrDefault(SCRIPTS_KEY, DatabaseValue.EMPTY_HASH);
    Map<SafeString, SafeString> scripts = value.getValue();
    return Optional.ofNullable(scripts.get(sha1));
  }

  public void cleanScripts() {
    admin.remove(SCRIPTS_KEY);
  }

  public Set<SafeString> getSlaves() {
    return getAdminDatabase().getSet(SLAVES);
  }

  public void addSlave(String id) {
    getAdminDatabase().merge(SLAVES_KEY, set(safeString(id)), (oldValue, newValue) -> {
      return set(oldValue.getSet().addAll(newValue.getSet()));
    });
  }

  public void removeSlave(String id) {
    getAdminDatabase().merge(SLAVES_KEY, set(safeString(id)), (oldValue, newValue) -> {
      return set(oldValue.getSet().removeAll(newValue.getSet()));
    });
  }

  public List<RedisToken> getCommandsToReplicate() {
    List<RedisToken> list = new LinkedList<>(queue);
    queue.clear();
    return list;
  }
}
