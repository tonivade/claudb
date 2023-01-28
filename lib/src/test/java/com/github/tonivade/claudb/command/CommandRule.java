/*
 * Copyright (c) 2015-2022, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.claudb.command;

import static com.github.tonivade.claudb.data.DatabaseKey.safeKey;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.github.tonivade.claudb.DBConfig;
import com.github.tonivade.claudb.DBConfig.Engine;
import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.DBServerState;
import com.github.tonivade.claudb.DBSessionState;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.claudb.data.DatabaseValue;
import com.github.tonivade.claudb.data.OnHeapMVDatabaseFactory;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.command.ServerContext;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import java.util.Optional;
import java.util.stream.Stream;

public class CommandRule implements TestRule {

  private Request request;
  private DBServerContext server;
  private Session session;
  private DBCommand dbCommand;

  private final Object target;

  private final DBServerState serverState = new DBServerState(new OnHeapMVDatabaseFactory(), 1);
  private final DBSessionState sessionState = new DBSessionState();
  private final DBConfig config = DBConfig.builder().withEngine(Engine.LUAJ).build();

  private RedisToken response;

  public CommandRule(Object target) {
    this.target = requireNonNull(target);
  }

  public Request getRequest() {
    return request;
  }

  public RedisToken getResponse() {
    return response;
  }

  public Database getDatabase() {
    return serverState.getDatabase(0);
  }

  public Session getSession() {
    return session;
  }

  public DBServerContext getServer() {
    return server;
  }

  public Database getAdminDatabase() {
    return serverState.getAdminDatabase();
  }

  @Override
  public Statement apply(final Statement base, final Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        server = mock(DBServerContext.class);
        request = mock(Request.class);
        session = mock(Session.class);

        when(request.getServerContext()).thenReturn(server);
        when(request.getSession()).thenReturn(session);

        when(session.getId()).thenReturn("localhost:12345");
        when(session.getValue("state")).thenReturn(Optional.of(sessionState));
        when(session.getValue("tx")).thenReturn(Optional.empty());
        when(session.removeValue("tx")).thenReturn(Optional.empty());

        when(server.getAdminDatabase()).thenReturn(serverState.getAdminDatabase());
        when(server.isMaster()).thenReturn(true);
        when(server.getValue("state")).thenReturn(Optional.of(serverState));
        when(server.getValue("config")).thenReturn(Optional.of(config));

        try (AutoCloseable openMocks = MockitoAnnotations.openMocks(target)) {
          dbCommand = target.getClass().getAnnotation(CommandUnderTest.class).value().getDeclaredConstructor().newInstance();

          base.evaluate();

          getDatabase().clear();
        }
      }
    };
  }

  public CommandRule withCommand(String name, RespCommand command) {
    Mockito.when(server.getCommand(name)).thenReturn(command);
    return this;
  }

  public CommandRule withData(String key, DatabaseValue value) {
    withData(getDatabase(), safeKey(key), value);
    return this;
  }

  public CommandRule withData(DatabaseKey key, DatabaseValue value) {
    withData(getDatabase(), key, value);
    return this;
  }

  public CommandRule withAdminData(String key, DatabaseValue value) {
    withData(getAdminDatabase(), safeKey(key), value);
    return this;
  }

  private void withData(Database database, DatabaseKey key, DatabaseValue value) {
    database.put(key, value);
  }

  public CommandRule execute() {
    response = new DBCommandWrapper(dbCommand).execute(request);
    return this;
  }

  public CommandRule withParams(String ... params) {
    if (params != null) {
      when(request.getParams()).thenReturn(Stream.of(params).map(SafeString::safeString).collect(toList()));
      int i = 0;
      for (String param : params) {
        when(request.getParam(i++)).thenReturn(safeString(param));
      }
      when(request.getParamsAsStream()).thenAnswer(invocation -> {
        return Stream.of(params).map(SafeString::safeString);
      });
      when(request.getLength()).thenReturn(params.length);
      when(request.getOptionalParam(anyInt())).thenAnswer(invocation -> {
        Integer param = (Integer) invocation.getArguments()[0];
        if (param < params.length) {
          return Optional.of(safeString(params[param]));
        }
        return Optional.empty();
      });
    }
    return this;
  }

  public CommandRule assertValue(String key, Matcher<DatabaseValue> matcher) {
    assertValue(getDatabase(), safeKey(key), matcher);
    return this;
  }

  public CommandRule assertAdminValue(String key, Matcher<DatabaseValue> matcher) {
    assertValue(getAdminDatabase(), safeKey(key), matcher);
    return this;
  }

  private void assertValue(Database database, DatabaseKey key, Matcher<DatabaseValue> matcher) {
    MatcherAssert.assertThat(database.get(key), matcher);
  }

  public CommandRule assertThat(RedisToken token) {
    assertThat(equalTo(token));
    return this;
  }

  public CommandRule assertThat(Matcher<? super RedisToken> matcher) {
    MatcherAssert.assertThat(response, matcher);
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T> T verify(Class<T> type) {
    if (type.equals(ServerContext.class)) {
      return (T) Mockito.verify(server);
    } else if (type.equals(DBServerContext.class)) {
      return (T) Mockito.verify(server);
    } else if (type.equals(Session.class)) {
      return (T) Mockito.verify(session);
    }
    throw new IllegalArgumentException();
  }

  public DBServerState getServerState() {
    return serverState;
  }

  public DBSessionState getSessionState() {
    return sessionState;
  }

}
