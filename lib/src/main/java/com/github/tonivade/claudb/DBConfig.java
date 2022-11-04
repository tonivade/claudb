/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

public class DBConfig {

  private static final int DEFAULT_CLEAN_PERIOD = 30;
  private static final int DEFAULT_DATABASES = 10;

  private int numDatabases = DEFAULT_DATABASES;

  private boolean persistenceActive;
  private boolean notificationsActive;
  private boolean offHeapActive;
  private boolean h2StorageActive;

  private int cleanPeriod = DEFAULT_CLEAN_PERIOD;

  public boolean isPersistenceActive() {
    return persistenceActive;
  }

  public void setPersistenceActive(boolean persistenceActive) {
    this.persistenceActive = persistenceActive;
  }

  public boolean isNotificationsActive() {
    return notificationsActive;
  }

  public void setNotificationsActive(boolean notificationsActive) {
    this.notificationsActive = notificationsActive;
  }

  public boolean isOffHeapActive() {
    return offHeapActive;
  }

  public void setOffHeapActive(boolean offHeapActive) {
    this.offHeapActive = offHeapActive;
  }

  public boolean isH2StorageActive() {
    return h2StorageActive;
  }

  public void setH2StorageActive(boolean h2StorageActive) {
    this.h2StorageActive = h2StorageActive;
  }

  public int getNumDatabases() {
    return numDatabases;
  }

  public void setNumDatabases(int numDatabases) {
    this.numDatabases = numDatabases;
  }

  public long getCleanPeriod() {
    return this.cleanPeriod;
  }

  public void setCleanPeriod(int cleanPeriod) {
    this.cleanPeriod = cleanPeriod;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private final DBConfig config = new DBConfig();

    public Builder withoutPersistence() {
      config.setPersistenceActive(false);
      return this;
    }

    public Builder withPersistence() {
      config.setPersistenceActive(true);
      return this;
    }

    public Builder withOffHeapCache() {
      config.setOffHeapActive(true);
      return this;
    }

    public Builder withH2Storage() {
      config.setH2StorageActive(true);
      return this;
    }

    public Builder withNotifications() {
      config.setNotificationsActive(true);
      return this;
    }

    public DBConfig build() {
      return config;
    }
  }
}
