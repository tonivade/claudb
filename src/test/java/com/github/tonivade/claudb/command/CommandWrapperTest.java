/*
 * Copyright (c) 2015-2021, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.claudb.command;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.responseOk;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.tonivade.claudb.DBServerContext;
import com.github.tonivade.claudb.DBServerState;
import com.github.tonivade.claudb.DBSessionState;
import com.github.tonivade.claudb.command.annotation.ParamType;
import com.github.tonivade.claudb.data.DataType;
import com.github.tonivade.claudb.data.Database;
import com.github.tonivade.claudb.data.DatabaseKey;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;

@RunWith(MockitoJUnitRunner.class)
public class CommandWrapperTest {
  @Mock
  private Database db;
  @Mock
  private Request request;
  @Mock
  private Session session;
  @Mock
  private DBServerContext server;
  @Mock
  private DBSessionState sessionState;
  @Mock
  private DBServerState serverState;

  @Before
  public void setUp() {
    when(request.getSession()).thenReturn(session);
    when(request.getServerContext()).thenReturn(server);
    when(request.getCommand()).thenReturn("test");
    when(session.getValue("state")).thenReturn(Option.some(sessionState));
    when(session.getValue("tx")).thenReturn(Option.none());
    when(server.getValue("state")).thenReturn(Option.some(serverState));
    when(sessionState.getCurrentDB()).thenReturn(1);
    when(serverState.getDatabase(1)).thenReturn(db);
  }

  @Test
  public void testExecute() {
    DBCommandWrapper wrapper = new DBCommandWrapper(new SomeCommand());

    RedisToken response = wrapper.execute(request);

    assertThat(response, equalTo(responseOk()));
  }

  @Test
  public void testLengthOK() {
    when(request.getLength()).thenReturn(3);

    DBCommandWrapper wrapper = new DBCommandWrapper(new LengthCommand());

    RedisToken response = wrapper.execute(request);

    assertThat(response, equalTo(responseOk()));
  }

  @Test
  public void testLengthKO() {
    when(request.getLength()).thenReturn(1);

    DBCommandWrapper wrapper = new DBCommandWrapper(new LengthCommand());

    RedisToken response = wrapper.execute(request);

    assertThat(response, equalTo(error("ERR wrong number of arguments for 'test' command")));
  }

  @Test
  public void testTypeOK() {
    when(db.isType(any(DatabaseKey.class), eq(DataType.STRING))).thenReturn(true);
    when(request.getParam(0)).thenReturn(safeString("test"));

    DBCommandWrapper wrapper = new DBCommandWrapper(new TypeCommand());

    RedisToken response = wrapper.execute(request);

    assertThat(response, equalTo(responseOk()));
  }

  @Test
  public void testTypeKO() {
    when(db.isType(any(DatabaseKey.class), eq(DataType.STRING))).thenReturn(false);
    when(request.getParam(0)).thenReturn(safeString("test"));

    DBCommandWrapper wrapper = new DBCommandWrapper(new TypeCommand());

    RedisToken response = wrapper.execute(request);

    assertThat(response, equalTo(error("WRONGTYPE Operation against a key holding the wrong kind of value")));
  }

  @Command("test")
  private static class SomeCommand implements DBCommand {
    @Override
    public RedisToken execute(Database db, Request request) {
      return responseOk();
    }
  }

  @Command("test")
  @ParamLength(2)
  private static class LengthCommand implements DBCommand {
    @Override
    public RedisToken execute(Database db, Request request) {
      return responseOk();
    }
  }

  @Command("test")
  @ParamType(DataType.STRING)
  private static class TypeCommand implements DBCommand {
    @Override
    public RedisToken execute(Database db, Request request) {
      return responseOk();
    }
  }
}
