/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tonivade.resp.RespServer;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.claudb.command.DBCommandSuite;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseCleaner;
import com.github.tonivade.claudb.data.DatabaseFactory;
import com.github.tonivade.claudb.data.OffHeapDatabaseFactory;
import com.github.tonivade.claudb.data.OnHeapDatabaseFactory;
import com.github.tonivade.claudb.event.Event;
import com.github.tonivade.claudb.event.NotificationManager;
import com.github.tonivade.claudb.persistence.PersistenceManager;

import io.reactivex.Observable;

public class ClauDB extends RespServer implements DBServerContext {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClauDB.class);

  private DatabaseCleaner cleaner;
  private Optional<PersistenceManager> persistence;
  private Optional<NotificationManager> notifications;
  
  private final DBConfig config;

  public ClauDB() {
    this(DEFAULT_HOST, DEFAULT_PORT);
  }

  public ClauDB(String host, int port) {
    this(host, port, DBConfig.builder().build());
  }

  public ClauDB(String host, int port, DBConfig config) {
    super(host, port, new DBCommandSuite());
    this.config = config;
  }

  @Override
  public void start() {
    super.start();

    init();

    getState().setMaster(true);

    persistence.ifPresent(PersistenceManager::start);
    notifications.ifPresent(NotificationManager::start);
    cleaner.start();
  }

  @Override
  public void stop() {
    super.stop();

    persistence.ifPresent(PersistenceManager::stop);
    notifications.ifPresent(NotificationManager::stop);
    cleaner.stop();

    getState().clear();

    persistence = null;
    notifications = null;
    cleaner = null;
  }

  @Override
  public List<RedisToken> getCommandsToReplicate() {
    return executeOn(Observable.<List<RedisToken>>create(observable -> {
      observable.onNext(getState().getCommandsToReplicate());
      observable.onComplete();
    })).blockingFirst();
  }

  @Override
  public void publish(String sourceKey, RedisToken message) {
    Session session = getSession(sourceKey);
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
    executeOn(Observable.create(observable -> {
      getState().exportRDB(output);
      observable.onComplete();
    })).blockingSubscribe();
  }

  @Override
  public void importRDB(InputStream input) throws IOException {
    executeOn(Observable.create(observable -> {
      getState().importRDB(input);
      observable.onComplete();
    })).blockingSubscribe();
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
  public void clean(Instant now) {
    executeOn(Observable.create(observable -> {
      getState().evictExpired(now);
      observable.onComplete();
    })).blockingSubscribe();  }

  @Override
  protected void createSession(Session session) {
    session.putValue("state", new DBSessionState());
  }

  @Override
  protected void cleanSession(Session session) {
    session.destroy();
  }

  @Override
  protected RedisToken executeCommand(RespCommand command, Request request) {
    if (!isReadOnly(request.getCommand())) {
      try {
        RedisToken response = command.execute(request);
        replication(request);
        notification(request);
        return response;
      } catch (RuntimeException e) {
        LOGGER.error("error executing command: " + request, e);
        return error("error executing command: " + request);
      }
    } else {
      return error("READONLY You can't write against a read only slave");
    }
  }

  private boolean isReadOnly(String command) {
    return !isMaster() && !isReadOnlyCommand(command);
  }

  private void replication(Request request) {
    if (!isReadOnlyCommand(request.getCommand())) {
      RedisToken array = requestToArray(request);
      if (hasSlaves()) {
        getState().append(array);
      }
      persistence.ifPresent(manager -> manager.append(array));
    }
  }

  private void notification(Request request) {
    if (!isReadOnlyCommand(request.getCommand()) && request.getLength() > 1) {
      notifications.ifPresent(manager -> publishEvent(manager, request));
    }
  }

  private boolean isReadOnlyCommand(String command) {
    return getDBCommands().isReadOnly(command);
  }

  private void publishEvent(NotificationManager manager, Request request) {
    manager.enqueue(createKeyEvent(request));
    manager.enqueue(createCommandEvent(request));
  }

  private Event createKeyEvent(Request request) {
    return Event.keyEvent(safeString(request.getCommand()), request.getParam(0), currentDB(request));
  }

  private Event createCommandEvent(Request request) {
    return Event.commandEvent(safeString(request.getCommand()), request.getParam(0), currentDB(request));
  }

  private Integer currentDB(Request request) {
    return getSessionState(request.getSession()).getCurrentDB();
  }

  private RedisToken requestToArray(Request request) {
    List<RedisToken> array = new LinkedList<>();
    array.add(currentDbToken(request));
    array.add(commandToken(request));
    array.addAll(paramTokens(request));
    return RedisToken.array(array);
  }

  private RedisToken commandToken(Request request) {
    return RedisToken.string(request.getCommand());
  }

  private RedisToken currentDbToken(Request request) {
    return RedisToken.string(valueOf(getCurrentDB(request)));
  }

  private int getCurrentDB(Request request) {
    return getSessionState(request.getSession()).getCurrentDB();
  }

  private List<RedisToken> paramTokens(Request request) {
    return request.getParams().stream().map(RedisToken::string).collect(toList());
  }

  private DBSessionState getSessionState(Session session) {
    return sessionState(session).orElseThrow(() -> new IllegalStateException("missing session state"));
  }

  private Optional<DBSessionState> sessionState(Session session) {
    return session.getValue("state");
  }

  private DBServerState getState() {
    return serverState().orElseThrow(() -> new IllegalStateException("missing server state"));
  }

  private Optional<DBServerState> serverState() {
    return getValue("state");
  }

  private boolean hasSlaves() {
    return getState().hasSlaves();
  }

  private DBCommandSuite getDBCommands() {
    return (DBCommandSuite) getCommands();
  }

  private void init()
  {
    DatabaseFactory factory = null;
    if (config.isOffHeapActive()) {
      factory = new OffHeapDatabaseFactory();
    } else {
      factory = new OnHeapDatabaseFactory();
    }

    putValue("state", new DBServerState(factory, config.getNumDatabases()));

    if (config.isPersistenceActive()) {
      this.persistence = Optional.of(new PersistenceManager(this, config));
    } else {
      this.persistence = Optional.empty();
    }
    if (config.isNotificationsActive()) {
      this.notifications = Optional.of(new NotificationManager(this));
    } else {
      this.notifications = Optional.empty();
    }
    this.cleaner = new DatabaseCleaner(this, config);
  }
}
