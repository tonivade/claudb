/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb;

public class TinyDBConfig {

  private static final int DEFAULT_SYNC_PERIOD = 30;
  private static final int DEFAULT_DATABASES = 10;
  private static final String DUMP_FILE = "dump.rdb";
  private static final String REDO_FILE = "redo.aof";

  private int numDatabases = DEFAULT_DATABASES;

  private boolean persistenceActive;
  private boolean notificationsActive;
  private boolean offHeapActive;

  private String rdbFile;
  private String aofFile;

  private int syncPeriod;

  public boolean isPersistenceActive() {
    return persistenceActive;
  }

  public void setPersistenceActive(boolean persistenceActive) {
    this.persistenceActive = persistenceActive;
  }

  public void setNotificationsActive(boolean notificationsActive) {
    this.notificationsActive = notificationsActive;
  }

  public boolean isNotificationsActive() {
    return notificationsActive;
  }
  
  public void setOffHeapActive(boolean offHeapActive) {
    this.offHeapActive = offHeapActive;
  }
  
  public boolean isOffHeapActive() {
    return offHeapActive;
  }

  public String getRdbFile() {
    return rdbFile;
  }

  public void setRdbFile(String rdbFile) {
    this.rdbFile = rdbFile;
  }

  public String getAofFile() {
    return aofFile;
  }

  public void setAofFile(String aofFile) {
    this.aofFile = aofFile;
  }

  public int getSyncPeriod() {
    return syncPeriod;
  }

  public void setSyncPeriod(int syncPeriod) {
    this.syncPeriod = syncPeriod;
  }

  public int getNumDatabases() {
    return numDatabases;
  }

  public void setNumDatabases(int numDatabases) {
    this.numDatabases = numDatabases;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    private TinyDBConfig config = new TinyDBConfig();

    public Builder withoutPersistence() {
      config.setPersistenceActive(false);
      return this;
    }
    
    public Builder withPersistence() {
      config.setPersistenceActive(true);
      config.setRdbFile(DUMP_FILE);
      config.setAofFile(REDO_FILE);
      config.setSyncPeriod(DEFAULT_SYNC_PERIOD);
      return this;
    }
    
    public Builder withOffHeapCache() {
      config.setOffHeapActive(true);
      return this;
    }

    public Builder withNotifications() {
      config.setNotificationsActive(true);
      return this;
    }
    
    public TinyDBConfig build() {
      return config;
    }
  }
}
