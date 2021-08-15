/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.persistence;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.string;
import static java.nio.ByteBuffer.wrap;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

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

import com.github.tonivade.claudb.DBConfig;
import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.DBSessionState;
import com.github.tonivade.claudb.command.DBCommandProcessor;
import com.github.tonivade.resp.command.DefaultSession;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.AbstractRedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.RedisParser;
import com.github.tonivade.resp.protocol.RedisSerializer;
import com.github.tonivade.resp.protocol.RedisSource;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisTokenType;
import com.github.tonivade.resp.protocol.SafeString;

public class PersistenceManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceManager.class);

  private static final int MAX_FRAME_SIZE = 1024 * 1024 * 100;

  private OutputStream output;
  private final DBServerContext server;
  private final DBCommandProcessor processor;
  private final String dumpFile;
  private final String redoFile;
  private final int syncPeriod;

  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  public PersistenceManager(DBServerContext server, DBConfig config) {
    this.server = requireNonNull(server);
    this.dumpFile = config.getRdbFile();
    this.redoFile = config.getAofFile();
    this.syncPeriod = config.getSyncPeriod();
    this.processor = new DBCommandProcessor(server, newDummySession());
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
        RedisParser parse = new RedisParser(MAX_FRAME_SIZE, new RedisSourceInputStream(redo));

        while (true) {
          RedisToken token = parse.next();
          if (token.getType() == RedisTokenType.UNKNOWN) {
            break;
          }
          LOGGER.info("command: {}", token);

          processCommand((ArrayRedisToken) token);
        }
      } catch (IOException e) {
        LOGGER.error("error reading AOF file", e);
      }
    }
  }

  private void processCommand(ArrayRedisToken array) {
    processor.processCommand((ArrayRedisToken) selectCommand(array));
    processor.processCommand((ArrayRedisToken) command(array));
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

  private RedisToken selectCommand(ArrayRedisToken token) {
    return array(string("select"), token.getValue().stream().findFirst().orElse(string("0")));
  }

  private RedisToken command(ArrayRedisToken token) {
    return array(token.getValue().stream().skip(1).collect(toList()));
  }

  private Session newDummySession() {
    DefaultSession session = new DefaultSession("dummy", null);
    session.putValue("state", new DBSessionState());
    return session;
  }
}

class RedisSourceInputStream implements RedisSource {

  private final InputStream input;

  RedisSourceInputStream(InputStream input) {
    this.input = requireNonNull(input);
  }

  @Override
  public int available() {
    try {
      return input.available();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public SafeString readLine() {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      boolean cr = false;
      while (true) {
        int read = input.read();

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
      byte[] buffer = new byte[size + 2];
      int red = input.read(buffer);
      if (red > -1) {
        return new SafeString(wrap(buffer, 0, red - 2));
      }
      return null;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}