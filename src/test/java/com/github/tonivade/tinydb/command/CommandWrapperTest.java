/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.responseOk;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.tinydb.TinyDBServerContext;
import com.github.tonivade.tinydb.TinyDBServerState;
import com.github.tonivade.tinydb.TinyDBSessionState;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.Database;
import com.github.tonivade.tinydb.data.DatabaseKey;

@RunWith(MockitoJUnitRunner.class)
public class CommandWrapperTest {
  @Mock
  private Database db;
  @Mock
  private Request request;
  @Mock
  private Session session;
  @Mock
  private TinyDBServerContext server;
  @Mock
  private TinyDBSessionState sessionState;
  @Mock
  private TinyDBServerState serverState;

  @Before
  public void setUp() {
    when(request.getSession()).thenReturn(session);
    when(request.getServerContext()).thenReturn(server);
    when(request.getCommand()).thenReturn("test");
    when(session.getValue("state")).thenReturn(Optional.of(sessionState));
    when(server.getValue("state")).thenReturn(Optional.of(serverState));
    when(sessionState.getCurrentDB()).thenReturn(1);
    when(serverState.getDatabase(1)).thenReturn(db);
  }

  @Test
  public void testExecute() {
    TinyDBCommandWrapper wrapper = new TinyDBCommandWrapper(new SomeCommand());

    RedisToken<?> response = wrapper.execute(request);

    assertThat(response, equalTo(responseOk()));
  }

  @Test
  public void testLengthOK() {
    when(request.getLength()).thenReturn(3);

    TinyDBCommandWrapper wrapper = new TinyDBCommandWrapper(new LengthCommand());

    RedisToken<?> response = wrapper.execute(request);

    assertThat(response, equalTo(responseOk()));
  }

  @Test
  public void testLengthKO() {
    when(request.getLength()).thenReturn(1);

    TinyDBCommandWrapper wrapper = new TinyDBCommandWrapper(new LengthCommand());

    RedisToken<?> response = wrapper.execute(request);

    assertThat(response, equalTo(error("ERR wrong number of arguments for 'test' command")));
  }

  @Test
  public void testTypeOK() {
    when(db.isType(any(DatabaseKey.class), eq(DataType.STRING))).thenReturn(true);
    when(request.getParam(0)).thenReturn(safeString("test"));

    TinyDBCommandWrapper wrapper = new TinyDBCommandWrapper(new TypeCommand());

    RedisToken<?> response = wrapper.execute(request);

    assertThat(response, equalTo(responseOk()));
  }

  @Test
  public void testTypeKO() {
    when(db.isType(any(DatabaseKey.class), eq(DataType.STRING))).thenReturn(false);
    when(request.getParam(0)).thenReturn(safeString("test"));

    TinyDBCommandWrapper wrapper = new TinyDBCommandWrapper(new TypeCommand());

    RedisToken<?> response = wrapper.execute(request);

    assertThat(response, equalTo(error("WRONGTYPE Operation against a key holding the wrong kind of value")));
  }

  @Command("test")
  private static class SomeCommand implements TinyDBCommand {
    @Override
    public RedisToken<?> execute(Database db, Request request) {
      return responseOk();
    }
  }

  @Command("test")
  @ParamLength(2)
  private static class LengthCommand implements TinyDBCommand {
    @Override
    public RedisToken<?> execute(Database db, Request request) {
      return responseOk();
    }
  }

  @Command("test")
  @ParamType(DataType.STRING)
  private static class TypeCommand implements TinyDBCommand {
    @Override
    public RedisToken<?> execute(Database db, Request request) {
      return responseOk();
    }
  }
}
