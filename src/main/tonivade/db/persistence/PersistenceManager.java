/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.persistence;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import tonivade.db.command.IServerContext;
import tonivade.db.command.Response;
import tonivade.db.redis.RedisArray;
import tonivade.db.redis.RedisToken;

/**
 * Saves and loads RDB files
 *
 * @author tomby
 *
 */
public class PersistenceManager implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(PersistenceManager.class.getName());

    private static final String DUMP_FILE = "dump.rdb";
    private static final String REDO_FILE = "redo.aof";

    private OutputStream output;

    private IServerContext server;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public PersistenceManager(IServerContext server) {
        this.server = server;
    }

    public void start() {
        importRDB();
        importRedo();
        createRedo();
        executor.scheduleWithFixedDelay(this, 30, 30, TimeUnit.SECONDS);
        LOGGER.info(() -> "Persistence manager started");
    }

    public void stop() {
        executor.shutdown();
        closeRedo();
        exportRDB();
        LOGGER.info(() -> "Persistence manager stopped");
    }

    @Override
    public void run() {
        exportRDB();
        createRedo();
    }

    public void append(RedisArray command) {
        if (output != null) {
            executor.submit(() -> appendRedo(command));
        }
    }

    private void importRDB() {
        File file = new File(DUMP_FILE);
        if (file.exists()) {
            try (FileInputStream rdb = new FileInputStream(file)) {
                server.importRDB(rdb);
                LOGGER.info(() -> "RDB file imported");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "error reading RDB", e);
            }
        }
    }

    private void importRedo() {
        File redo = new File(REDO_FILE);
        if (redo.exists()) {

        }
    }

    private void createRedo() {
        try {
            closeRedo();
            output = new FileOutputStream(REDO_FILE);
            LOGGER.info(() -> "AOF file created");
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private void closeRedo() {
        try {
            if (output != null) {
                output.close();
                output = null;
                LOGGER.fine(() -> "AOF file closed");
            }
        } catch (IOException e) {
            LOGGER.severe("error closing file");
        }
    }

    private void exportRDB() {
        try (FileOutputStream rdb = new FileOutputStream(DUMP_FILE)) {
            server.exportRDB(rdb);
            LOGGER.info(() -> "RDB file exported");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "error writing to RDB file", e);
        }
    }

    private void appendRedo(RedisArray command) {
        try {
            Response response = new Response();
            response.addArray(command.stream().map(RedisToken::getValue).collect(toList()));
            output.write(response.getBytes());
            output.flush();
            LOGGER.fine(() -> "new command: " + command);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "error writing to AOF file", e);
        }
    }

}
