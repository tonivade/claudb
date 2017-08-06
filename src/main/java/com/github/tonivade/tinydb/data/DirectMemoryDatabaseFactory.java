package com.github.tonivade.tinydb.data;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

public class DirectMemoryDatabaseFactory implements DatabaseFactory {
  private final DB db = DBMaker.memoryDirectDB().make();

  @Override
  public Database create(String name) {
    return new SimpleDatabase(createMap(name));
  }

  @Override
  public void clear() {
    db.close();
  }

  @SuppressWarnings("unchecked")
  private BTreeMap<DatabaseKey, DatabaseValue> createMap(String name) {
    return db.treeMap(name)
        .keySerializer(Serializer.JAVA)
        .valueSerializer(Serializer.JAVA)
        .create();
  }
}
