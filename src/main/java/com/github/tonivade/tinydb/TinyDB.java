/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.tinydb;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.tinydb.TinyDBConfig.withoutPersistence;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.tonivade.resp.RespServer;
import com.github.tonivade.resp.command.ICommand;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.RedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.RedisToken.StringRedisToken;
import com.github.tonivade.tinydb.command.TinyDBCommandSuite;
import com.github.tonivade.tinydb.command.annotation.ReadOnly;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.persistence.PersistenceManager;

public class TinyDB extends RespServer implements ITinyDB {

  private static final Logger LOGGER = Logger.getLogger(TinyDB.class.getName());

  private final BlockingQueue<ArrayRedisToken> queue = new LinkedBlockingQueue<>();

  private final Optional<PersistenceManager> persistence;

  public TinyDB() {
    this(DEFAULT_HOST, DEFAULT_PORT);
  }

  public TinyDB(String host, int port) {
    this(host, port, withoutPersistence());
  }

  public TinyDB(String host, int port, TinyDBConfig config) {
    super(host, port, new TinyDBCommandSuite());
    if (config.isPersistenceActive()) {
      this.persistence = Optional.of(new PersistenceManager(this, config));
    } else {
      this.persistence = Optional.empty();
    }
    putValue("state", new TinyDBServerState(config.getNumDatabases()));
  }

  @Override
  public void start() {
    super.start();
  }

  @Override
  public void stop() {
    super.stop();

    queue.clear();

    LOGGER.info(() -> "server stopped");
  }

  @Override
  public List<ArrayRedisToken> getCommandsToReplicate() {
    List<ArrayRedisToken> current = new LinkedList<>();
    queue.drainTo(current);
    return current;
  }

  @Override
  public void publish(String sourceKey, RedisToken<?> message) {
    ISession session = getSession(sourceKey);
    if (session != null) {
      session.publish(message);
    }
  }

  @Override
  public Database getAdminDatabase() {
    return getState().getAdminDatabase();
  }

  @Override
  public Database getDatabase(int i) {
    return getState().getDatabase(i);
  }

  @Override
  public void exportRDB(OutputStream output) throws IOException {
    getState().exportRDB(output);
  }

  @Override
  public void importRDB(InputStream input) throws IOException {
    getState().importRDB(input);
  }

  @Override
  public boolean isMaster() {
    return getState().isMaster();
  }

  @Override
  public void setMaster(boolean master) {
    getState().setMaster(master);
  }

  @Override
  protected void createSession(ISession session) {
    session.putValue("state", new TinyDBSessionState());
  }

  @Override
  protected void cleanSession(ISession session) {
    session.destroy();
  }

  @Override
  protected RedisToken<?> executeCommand(ICommand command, IRequest request) {
    if (!isReadOnly(request.getCommand())) {
      try {
        RedisToken<?> response = command.execute(request);
        replication(request, command);
        return response;
      } catch (RuntimeException e) {
        LOGGER.log(Level.SEVERE, "error executing command: " + request, e);
        return error("error executing command: " + request);
      }
    } else {
      return error("READONLY You can't write against a read only slave");
    }
  }

  private boolean isReadOnly(String command) {
    return !isMaster() && !isReadOnlyCommand(command);
  }

  private void replication(IRequest request, ICommand command) {
    if (!isReadOnlyCommand(request.getCommand())) {
      ArrayRedisToken array = requestToArray(request);
      if (hasSlaves()) {
        queue.add(array);
      }
      persistence.ifPresent(p -> p.append(array));
    }
  }

  private boolean isReadOnlyCommand(String command) {
    return getCommands().isPresent(command, ReadOnly.class);
  }

  private ArrayRedisToken requestToArray(IRequest request) {
    List<RedisToken<?>> array = new LinkedList<>();
    array.add(currentDbToken(request));
    array.add(commandToken(request));
    array.addAll(paramTokens(request));
    return RedisToken.array(array);
  }

  private StringRedisToken commandToken(IRequest request) {
    return RedisToken.string(request.getCommand());
  }

  private StringRedisToken currentDbToken(IRequest request) {
    return RedisToken.string(valueOf(getCurrentDB(request)));
  }

  private int getCurrentDB(IRequest request) {
    return getSessionState(request.getSession()).getCurrentDB();
  }

  private List<RedisToken<?>> paramTokens(IRequest request) {
    return request.getParams().stream().map(RedisToken::string).collect(toList());
  }

  private TinyDBSessionState getSessionState(ISession session) {
    return sessionState(session).orElseThrow(() -> new IllegalStateException("missing session state"));
  }

  private Optional<TinyDBSessionState> sessionState(ISession session) {
    return session.getValue("state");
  }

  private TinyDBServerState getState() {
    return serverState().orElseThrow(() -> new IllegalStateException("missing server state"));
  }

  private Optional<TinyDBServerState> serverState() {
    return getValue("state");
  }

  private boolean hasSlaves() {
    return getState().hasSlaves();
  }

}
