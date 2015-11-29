/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.redis.command.IResponse.RESULT_OK;
import static tonivade.redis.protocol.SafeString.safeString;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.ITinyDB;
import tonivade.db.TinyDBServerState;
import tonivade.db.TinyDBSessionState;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.IDatabase;
import tonivade.redis.annotation.ParamLength;
import tonivade.redis.command.IRequest;
import tonivade.redis.command.IResponse;
import tonivade.redis.command.ISession;

@RunWith(MockitoJUnitRunner.class)
public class CommandWrapperTest {

    @Mock
    private IDatabase db;

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Mock
    private ISession session;

    @Mock
    private ITinyDB server;

    @Mock
    private TinyDBSessionState sessionState;

    @Mock
    private TinyDBServerState serverState;

    @Before
    public void setUp() {
        when(request.getSession()).thenReturn(session);
        when(request.getServerContext()).thenReturn(server);
        when(session.getValue("state")).thenReturn(sessionState);
        when(server.getValue("state")).thenReturn(serverState);
        when(sessionState.getCurrentDB()).thenReturn(1);
        when(serverState.getDatabase(1)).thenReturn(db);
    }

    @Test
    public void testExecute() {
        RedisCommandWrapper wrapper = new RedisCommandWrapper(new SomeCommand());

        wrapper.execute(request, response);

        verify(response).addSimpleStr(RESULT_OK);
    }

    @Test
    public void testLengthOK() {
        when(request.getLength()).thenReturn(3);

        RedisCommandWrapper wrapper = new RedisCommandWrapper(new LengthCommand());

        wrapper.execute(request, response);

        verify(response).addSimpleStr(RESULT_OK);
    }

    @Test
    public void testLengthKO() {
        when(request.getLength()).thenReturn(1);

        RedisCommandWrapper wrapper = new RedisCommandWrapper(new LengthCommand());

        wrapper.execute(request, response);

        verify(response, times(0)).addSimpleStr(RESULT_OK);

        verify(response).addError(anyString());
    }

    @Test
    public void testTypeOK() {
        when(db.isType(any(DatabaseKey.class), eq(DataType.STRING))).thenReturn(true);
        when(request.getParam(0)).thenReturn(safeString("test"));

        RedisCommandWrapper wrapper = new RedisCommandWrapper(new TypeCommand());

        wrapper.execute(request, response);

        verify(response).addSimpleStr(RESULT_OK);
    }

    @Test
    public void testTypeKO() {
        when(db.isType(any(DatabaseKey.class), eq(DataType.STRING))).thenReturn(false);
        when(request.getParam(0)).thenReturn(safeString("test"));

        RedisCommandWrapper wrapper = new RedisCommandWrapper(new TypeCommand());

        wrapper.execute(request, response);

        verify(response, times(0)).addSimpleStr(RESULT_OK);

        verify(response).addError(anyString());
    }

    private static class SomeCommand implements IRedisCommand {
        @Override
        public void execute(IDatabase db, IRequest request, IResponse response) {
            response.addSimpleStr(RESULT_OK);
        }
    }

    @ParamLength(2)
    private static class LengthCommand implements IRedisCommand {
        @Override
        public void execute(IDatabase db, IRequest request, IResponse response) {
            response.addSimpleStr(RESULT_OK);
        }
    }

    @ParamType(DataType.STRING)
    private static class TypeCommand implements IRedisCommand {
        @Override
        public void execute(IDatabase db, IRequest request, IResponse response) {
            response.addSimpleStr(RESULT_OK);
        }
    }

}
