/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db;

public class TinyDBConfig {

    private static final int DEFAULT_SYNC_PERIOD = 30;
    private static final int DEFAULT_DATABASES = 10;
    private static final String DUMP_FILE = "dump.rdb";
    private static final String REDO_FILE = "redo.aof";

    private int numDatabases;

    private boolean persistenceActive;

    private String rdbFile;
    private String aofFile;

    private int syncPeriod;

    /**
     * @return the persistenceActive
     */
    public boolean isPersistenceActive() {
        return persistenceActive;
    }

    /**
     * @param persistenceActive the persistenceActive to set
     */
    public void setPersistenceActive(boolean persistenceActive) {
        this.persistenceActive = persistenceActive;
    }

    /**
     * @return the rdbFile
     */
    public String getRdbFile() {
        return rdbFile;
    }

    /**
     * @param rdbFile the rdbFile to set
     */
    public void setRdbFile(String rdbFile) {
        this.rdbFile = rdbFile;
    }

    /**
     * @return the aofFile
     */
    public String getAofFile() {
        return aofFile;
    }

    /**
     * @param aofFile the aofFile to set
     */
    public void setAofFile(String aofFile) {
        this.aofFile = aofFile;
    }

    /**
     * @return the syncPeriod
     */
    public int getSyncPeriod() {
        return syncPeriod;
    }

    /**
     * @param syncPeriod the syncPeriod to set
     */
    public void setSyncPeriod(int syncPeriod) {
        this.syncPeriod = syncPeriod;
    }

    /**
     * @return the numDatabases
     */
    public int getNumDatabases() {
        return numDatabases;
    }

    /**
     * @param numDatabases the numDatabases to set
     */
    public void setNumDatabases(int numDatabases) {
        this.numDatabases = numDatabases;
    }

    public static TinyDBConfig withoutPersistence() {
        TinyDBConfig config = new TinyDBConfig();
        config.setNumDatabases(DEFAULT_DATABASES);
        config.setPersistenceActive(false);
        return config;
    }

    public static TinyDBConfig withPersistence() {
        TinyDBConfig config = new TinyDBConfig();
        config.setNumDatabases(DEFAULT_DATABASES);
        config.setPersistenceActive(true);
        config.setRdbFile(DUMP_FILE);
        config.setAofFile(REDO_FILE);
        config.setSyncPeriod(DEFAULT_SYNC_PERIOD);
        return config;
    }

}
