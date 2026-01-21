/*
 * Copyright (c) 2015-2025, Antonio Gabriel Muñoz Conejo <me at tonivade dot es>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static com.github.tonivade.resp.util.Precondition.checkNonNull;
import com.github.tonivade.claudb.command.DBCommandSuite;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseCleaner;
import com.github.tonivade.claudb.data.DatabaseFactory;
import com.github.tonivade.claudb.data.OffHeapMVDatabaseFactory;
import com.github.tonivade.claudb.data.OnHeapMVDatabaseFactory;
import com.github.tonivade.claudb.data.PersistentMVDatabaseFactory;
import com.github.tonivade.claudb.event.Event;
import com.github.tonivade.claudb.event.NotificationManager;
import com.github.tonivade.resp.RespServer;
import com.github.tonivade.resp.RespServerContext;
import com.github.tonivade.resp.SessionListener;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.util.Recoverable;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.reactivex.rxjava3.core.Observable;

public final class ClauDB extends RespServerContext implements DBServerContext {

  private static final String CONFIG = "config";
  private static final String STATE = "state";

  private static final Logger LOGGER = LoggerFactory.getLogger(ClauDB.class);

  private DatabaseCleaner cleaner;
  private Optional<NotificationManager> notifications;

  private final DBConfig config;

  public ClauDB() {
    this(DEFAULT_HOST, DEFAULT_PORT);
  }

  public ClauDB(String host, int port) {
    this(host, port, DBConfig.builder().build());
  }

  public ClauDB(String host, int port, DBConfig config) {
    super(host, port, new DBCommandSuite(), new DBSessionListener());
    this.config = checkNonNull(config);
  }

  public static ClauDB.Builder builder() {
    return new Builder();
  }

  @Override
  public void start() {
    super.start();

    init();

    notifications.ifPresent(NotificationManager::start);
    cleaner.start();
  }

  @Override
  public void stop() {
    notifications.ifPresent(NotificationManager::stop);
    cleaner.stop();

    getState().clear();

    notifications = null;
    cleaner = null;

    super.stop();
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
  public void clean(Instant now) {
    enqueue(Observable.create(observable -> {
      getState().evictExpired(now);
      observable.onComplete();
    })).blockingSubscribe();
  }

  @Override
  protected RedisToken executeCommand(RespCommand command, Request request) {
    try {
      RedisToken response = command.execute(request);
      notification(request);
      return response;
    } catch (RuntimeException e) {
      LOGGER.error("error executing command: " + request, e);
      return error("error executing command: " + request);
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

  private DBSessionState getSessionState(Session session) {
    return sessionState(session).orElseThrow(() -> new IllegalStateException("missing session state"));
  }

  private Optional<DBSessionState> sessionState(Session session) {
    return session.getValue(STATE);
  }

  private DBServerState getState() {
    return serverState().orElseThrow(() -> new IllegalStateException("missing server state"));
  }

  private Optional<DBServerState> serverState() {
    return getValue(STATE);
  }

  private DBCommandSuite getDBCommands() {
    return (DBCommandSuite) getCommands();
  }

  private void init() {
    DatabaseFactory factory = initFactory();

    putValue(STATE, new DBServerState(factory, config.getNumDatabases()));
    putValue(CONFIG, config);

    initNotifications();
    initCleaner();
  }

  private void initCleaner() {
    this.cleaner = new DatabaseCleaner(this, config);
  }

  private void initNotifications() {
    if (config.isNotificationsActive()) {
      this.notifications = Optional.of(new NotificationManager(this));
    } else {
      this.notifications = Optional.empty();
    }
  }

  private DatabaseFactory initFactory() {
    DatabaseFactory factory;
    if (config.isOffHeapActive()) {
      factory = new OffHeapMVDatabaseFactory(config.getCacheConcurrency());
    } else if (config.isPersistenceActive()) {
      factory = new PersistentMVDatabaseFactory(config.getFileName(), config.getCacheConcurrency());
    } else {
      factory = new OnHeapMVDatabaseFactory();
    }
    return factory;
  }

  private static final class DBSessionListener implements SessionListener {
    @Override
    public void sessionDeleted(Session session) {
      session.destroy();
    }

    @Override
    public void sessionCreated(Session session) {
      session.putValue(STATE, new DBSessionState());
    }
  }

  public static class Builder implements Recoverable {

    private String host = DEFAULT_HOST;
    private int port = DEFAULT_PORT;
    private DBConfig.Builder config = DBConfig.builder();

    public Builder host(String host) {
      this.host = host;
      return this;
    }

    public Builder port(int port) {
      this.port = port;
      return this;
    }

    public Builder randomPort() {
      try (ServerSocket socket = new ServerSocket(0)) {
        socket.setReuseAddress(true);
        this.port = socket.getLocalPort();
      } catch (IOException e) {
        return sneakyThrow(e);
      }
      return this;
    }

    public Builder withoutPersistence() {
      config.withoutPersistence();
      return this;
    }

    public Builder withPersistence(String fileName) {
      config.withPersistence(fileName);
      return this;
    }

    public Builder withOffHeapCache() {
      config.withOffHeapCache();
      return this;
    }

    public Builder withNotifications() {
      config.withNotifications();
      return this;
    }

    public Builder withEngine(DBConfig.Engine engine) {
      config.withEngine(engine);
      return this;
    }

    public Builder config(DBConfig.Builder config) {
      this.config = config;
      return this;
    }

    public RespServer build() {
      return new RespServer(new ClauDB(host, port, config.build()));
    }
  }
}
