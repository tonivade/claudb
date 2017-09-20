/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.persistence;

import static java.nio.ByteBuffer.wrap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tonivade.resp.protocol.AbstractRedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.RedisParser;
import com.github.tonivade.resp.protocol.RedisSerializer;
import com.github.tonivade.resp.protocol.RedisSource;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisTokenType;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.claudb.TinyDBConfig;
import com.github.tonivade.claudb.TinyDBServerContext;
import com.github.tonivade.claudb.command.TinyDBCommandProcessor;

public class PersistenceManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceManager.class);

  private static final int MAX_FRAME_SIZE = 1024 * 1024 * 100;

  private OutputStream output;
  private final TinyDBServerContext server;
  private final TinyDBCommandProcessor processor;
  private final String dumpFile;
  private final String redoFile;
  private final int syncPeriod;

  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  public PersistenceManager(TinyDBServerContext server, TinyDBConfig config) {
    this.server = server;
    this.dumpFile = config.getRdbFile();
    this.redoFile = config.getAofFile();
    this.syncPeriod = config.getSyncPeriod();
    this.processor = new TinyDBCommandProcessor(server);
  }

  public void start() {
    importRDB();
    importRedo();
    createRedo();
    executor.scheduleWithFixedDelay(this::run, syncPeriod, syncPeriod, TimeUnit.SECONDS);
    LOGGER.info("Persistence manager started");
  }

  public void stop() {
    executor.shutdown();
    closeRedo();
    exportRDB();
    LOGGER.info("Persistence manager stopped");
  }

  void run() {
    exportRDB();
    createRedo();
  }

  public void append(RedisToken command) {
    if (output != null) {
      executor.submit(() -> appendRedo(command));
    }
  }

  private void importRDB() {
    File file = new File(dumpFile);
    if (file.exists()) {
      try (InputStream rdb = new FileInputStream(file)) {
        server.importRDB(rdb);
        LOGGER.info("RDB file imported");
      } catch (IOException e) {
        LOGGER.error("error reading RDB", e);
      }
    }
  }

  private void importRedo() {
    File file = new File(redoFile);
    if (file.exists()) {
      try (FileInputStream redo = new FileInputStream(file)) {
        RedisParser parse = new RedisParser(MAX_FRAME_SIZE, new InputStreamRedisSource(redo));

        while (true) {
          RedisToken token = parse.parse();
          if (token.getType() == RedisTokenType.UNKNOWN) {
            break;
          }
          processor.processCommand((ArrayRedisToken) token);
        }
      } catch (IOException e) {
        LOGGER.error("error reading AOF file", e);
      }
    }
  }

  private void createRedo() {
    try {
      closeRedo();
      output = new FileOutputStream(redoFile);
      LOGGER.info("AOF file created");
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void closeRedo() {
    try {
      if (output != null) {
        output.close();
        output = null;
        LOGGER.debug("AOF file closed");
      }
    } catch (IOException e) {
      LOGGER.error("error closing AOF file", e);
    }
  }

  private void exportRDB() {
    try (FileOutputStream rdb = new FileOutputStream(dumpFile)) {
      server.exportRDB(rdb);
      LOGGER.info("RDB file exported");
    } catch (IOException e) {
      LOGGER.error("error writing to RDB file", e);
    }
  }

  private void appendRedo(RedisToken command) {
    try {
      RedisSerializer serializer = new RedisSerializer();
      byte[] buffer = serializer.encodeToken(command);
      output.write(buffer);
      output.flush();
      LOGGER.debug("new command: " + command);
    } catch (IOException e) {
      LOGGER.error("error writing to AOF file", e);
    }
  }

  private static class InputStreamRedisSource implements RedisSource {

    private final InputStream stream;

    public InputStreamRedisSource(InputStream stream) {
      super();
      this.stream = stream;
    }

    @Override
    public SafeString readLine() {
      try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean cr = false;
        while (true) {
          int read = stream.read();

          if (read == -1) {
            // end of stream
            break;
          }

          if (read == '\r') {
            cr = true;
          } else if (cr && read == '\n') {
            break;
          } else {
            cr = false;

            baos.write(read);
          }
        }
        return new SafeString(baos.toByteArray());
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    @Override
    public SafeString readString(int size) {
      try {
        byte[] buffer = new byte[size];
        int readed = stream.read(buffer);
        if (readed > -1) {
          return new SafeString(wrap(buffer, 0, readed));
        }
        return null;
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }
}
