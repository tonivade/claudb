/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb;

import static com.github.tonivade.purefun.data.Sequence.listOf;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.lang.String.valueOf;
import com.github.tonivade.claudb.command.DBCommandSuite;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseCleaner;
import com.github.tonivade.claudb.data.DatabaseFactory;
import com.github.tonivade.claudb.data.OffHeapMVDatabaseFactory;
import com.github.tonivade.claudb.data.OnHeapMVDatabaseFactory;
import com.github.tonivade.claudb.data.PersistentMVDatabaseFactory;
import com.github.tonivade.claudb.event.Event;
import com.github.tonivade.claudb.event.NotificationManager;
import com.github.tonivade.purefun.Recoverable;
import com.github.tonivade.purefun.data.ImmutableArray;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.resp.RespServer;
import com.github.tonivade.resp.RespServerContext;
import com.github.tonivade.resp.SessionListener;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.reactivex.rxjava3.core.Observable;

public final class ClauDB extends RespServerContext implements DBServerContext {

  private static final String INTERPRETER = "interpreter";
  private static final String STATE = "state";

  private static final Logger LOGGER = LoggerFactory.getLogger(ClauDB.class);

  private DatabaseCleaner cleaner;
  private Option<NotificationManager> notifications;

  private final DBConfig config;

  public ClauDB() {
    this(DEFAULT_HOST, DEFAULT_PORT);
  }

  public ClauDB(String host, int port) {
    this(host, port, DBConfig.builder().build());
  }

  public ClauDB(String host, int port, DBConfig config) {
    super(host, port, new DBCommandSuite(), new DBSessionListener());
    this.config = config;
  }

  public static ClauDB.Builder builder() {
    return new Builder();
  }

  @Override
  public void start() {
    super.start();

    init();

    getState().setMaster(true);

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
  public ImmutableList<RedisToken> getCommandsToReplicate() {
    return executeOn(Observable.<ImmutableList<RedisToken>>create(observable -> {
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
  public void exportRDB(OutputStream output) {
    executeOn(Observable.create(observable -> {
      getState().exportRDB(output);
      observable.onComplete();
    })).blockingSubscribe();
  }

  @Override
  public void importRDB(InputStream input) {
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
    })).blockingSubscribe();
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
    return RedisToken.array(listOf(currentDbToken(request))
        .append(commandToken(request))
        .appendAll(paramTokens(request)));
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

  private ImmutableArray<RedisToken> paramTokens(Request request) {
    return request.getParams().map(RedisToken::string);
  }

  private DBSessionState getSessionState(Session session) {
    return sessionState(session).getOrElseThrow(() -> new IllegalStateException("missing session state"));
  }

  private Option<DBSessionState> sessionState(Session session) {
    return session.getValue(STATE);
  }

  private DBServerState getState() {
    return serverState().getOrElseThrow(() -> new IllegalStateException("missing server state"));
  }

  private Option<DBServerState> serverState() {
    return getValue(STATE);
  }

  private boolean hasSlaves() {
    return getState().hasSlaves();
  }

  private DBCommandSuite getDBCommands() {
    return (DBCommandSuite) getCommands();
  }

  private void init() {
    DatabaseFactory factory = initFactory();

    putValue(STATE, new DBServerState(factory, config.getNumDatabases()));
    putValue(INTERPRETER, config.getEngine());

    initNotifications();
    initCleaner();
  }

  private void initCleaner() {
    this.cleaner = new DatabaseCleaner(this, config);
  }

  private void initNotifications() {
    if (config.isNotificationsActive()) {
      this.notifications = Option.some(new NotificationManager(this));
    } else {
      this.notifications = Option.none();
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

    public Builder config(DBConfig.Builder config) {
      this.config = config;
      return this;
    }

    public RespServer build() {
      return new RespServer(new ClauDB(host, port, config.build()));
    }
  }
}
