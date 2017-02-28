/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.persistence;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static java.nio.ByteBuffer.wrap;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.tonivade.resp.command.ICommand;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.Response;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisParser;
import com.github.tonivade.resp.protocol.RedisSerializer;
import com.github.tonivade.resp.protocol.RedisSource;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisTokenType;
import com.github.tonivade.resp.protocol.SafeString;
import com.github.tonivade.tinydb.ITinyDB;
import com.github.tonivade.tinydb.TinyDBConfig;

public class PersistenceManager implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(PersistenceManager.class.getName());

  private static final int MAX_FRAME_SIZE = 1024 * 1024 * 100;

  private OutputStream output;
  private final ITinyDB server;
  private final String dumpFile;
  private final String redoFile;

  private final int syncPeriod;

  private final ISession session = new Session("dummy", null);

  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  public PersistenceManager(ITinyDB server, TinyDBConfig config) {
    this.server = server;
    this.dumpFile = config.getRdbFile();
    this.redoFile = config.getAofFile();
    this.syncPeriod = config.getSyncPeriod();
  }

  public void start() {
    importRDB();
    importRedo();
    createRedo();
    executor.scheduleWithFixedDelay(this, syncPeriod, syncPeriod, TimeUnit.SECONDS);
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

  public void append(List<RedisToken> command) {
    if (output != null) {
      executor.submit(() -> appendRedo(command));
    }
  }

  private void importRDB() {
    File file = new File(dumpFile);
    if (file.exists()) {
      try (InputStream rdb = new FileInputStream(file)) {
        server.importRDB(rdb);
        LOGGER.info(() -> "RDB file imported");
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "error reading RDB", e);
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
          processCommand(token);
        }
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "error reading RDB", e);
      }
    }
  }

  private void processCommand(RedisToken token) {
    List<RedisToken> array = token.<List<RedisToken>>getValue();
    RedisToken commandToken = array.get(0);
    List<RedisToken> paramTokens = array.stream().skip(1).collect(toList());

    LOGGER.fine(() -> "command recieved from master: " + commandToken.getValue());

    ICommand command = server.getCommand(commandToken.getValue().toString());

    if (command != null) {
      command.execute(request(commandToken, paramTokens), new Response());
    }
  }

  private Request request(RedisToken commandToken, List<RedisToken> array) {
    return new Request(server, session, commandToken.getValue(), arrayToList(array));
  }

  private List<SafeString> arrayToList(List<RedisToken> request) {
    List<SafeString> cmd = new LinkedList<>();
    for (RedisToken token : request) {
      cmd.add(token.<SafeString>getValue());
    }
    return cmd;
  }

  private void createRedo() {
    try {
      closeRedo();
      output = new FileOutputStream(redoFile);
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
    try (FileOutputStream rdb = new FileOutputStream(dumpFile)) {
      server.exportRDB(rdb);
      LOGGER.info(() -> "RDB file exported");
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "error writing to RDB file", e);
    }
  }

  private void appendRedo(List<RedisToken> command) {
    try {
      RedisSerializer serializer = new RedisSerializer();
      byte[] buffer = serializer.encodeToken(array(command));
      output.write(buffer);
      output.flush();
      LOGGER.fine(() -> "new command: " + command);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "error writing to AOF file", e);
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
        throw new IOError(e);
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
        throw new IOError(e);
      }
    }
  }

}
