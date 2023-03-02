/*
 * Copyright (c) 2015-2023, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

public class DBConfig {

  public static final int DEFAULT_CLEAN_PERIOD = 30;
  public static final int DEFAULT_DATABASES = 10;
  public static final int DEFAULT_SEGMENTS = 16;
  public static final String DEFAULT_FILENAME = "./claudb.data";

  public enum Engine {
    LUAJ, JAVASCRIPT, NULL
  }

  private int numDatabases = DEFAULT_DATABASES;
  private int cleanPeriod = DEFAULT_CLEAN_PERIOD;

  private boolean persistenceActive;
  private boolean notificationsActive;
  private boolean offHeapActive;

  private String fileName = DEFAULT_FILENAME;
  private int cacheConcurrency = DEFAULT_SEGMENTS;

  private Engine engine = Engine.NULL;

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

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }

  public void setCacheConcurrency(int cacheConcurrency) {
    this.cacheConcurrency = cacheConcurrency;
  }

  public int getCacheConcurrency() {
    return cacheConcurrency;
  }

  public void setEngine(Engine engine) {
    this.engine = engine;
  }

  public Engine getEngine() {
    return engine;
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

    public Builder withPersistence(String fileName) {
      config.setPersistenceActive(true);
      config.setFileName(fileName);
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

    public Builder withEngine(Engine engine) {
      config.setEngine(engine);
      return this;
    }

    public DBConfig build() {
      return config;
    }
  }
}
