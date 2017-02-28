/*
 * Copyright (c) 2015-2017, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package com.github.tonivade.tinydb.command;

import static com.github.tonivade.resp.command.IResponse.RESULT_OK;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.IRequest;
import com.github.tonivade.resp.command.IResponse;
import com.github.tonivade.resp.command.ISession;
import com.github.tonivade.tinydb.ITinyDB;
import com.github.tonivade.tinydb.TinyDBServerState;
import com.github.tonivade.tinydb.TinyDBSessionState;
import com.github.tonivade.tinydb.command.annotation.ParamType;
import com.github.tonivade.tinydb.data.DataType;
import com.github.tonivade.tinydb.data.DatabaseKey;
import com.github.tonivade.tinydb.data.IDatabase;

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
        when(session.getValue("state")).thenReturn(Optional.of(sessionState));
        when(server.getValue("state")).thenReturn(Optional.of(serverState));
        when(sessionState.getCurrentDB()).thenReturn(1);
        when(serverState.getDatabase(1)).thenReturn(db);
    }

    @Test
    public void testExecute() {
        TinyDBCommandWrapper wrapper = new TinyDBCommandWrapper(new SomeCommand());

        wrapper.execute(request, response);

        verify(response).addSimpleStr(RESULT_OK);
    }

    @Test
    public void testLengthOK() {
        when(request.getLength()).thenReturn(3);

        TinyDBCommandWrapper wrapper = new TinyDBCommandWrapper(new LengthCommand());

        wrapper.execute(request, response);

        verify(response).addSimpleStr(RESULT_OK);
    }

    @Test
    public void testLengthKO() {
        when(request.getLength()).thenReturn(1);

        TinyDBCommandWrapper wrapper = new TinyDBCommandWrapper(new LengthCommand());

        wrapper.execute(request, response);

        verify(response, times(0)).addSimpleStr(RESULT_OK);

        verify(response).addError(anyString());
    }

    @Test
    public void testTypeOK() {
        when(db.isType(any(DatabaseKey.class), eq(DataType.STRING))).thenReturn(true);
        when(request.getParam(0)).thenReturn(safeString("test"));

        TinyDBCommandWrapper wrapper = new TinyDBCommandWrapper(new TypeCommand());

        wrapper.execute(request, response);

        verify(response).addSimpleStr(RESULT_OK);
    }

    @Test
    public void testTypeKO() {
        when(db.isType(any(DatabaseKey.class), eq(DataType.STRING))).thenReturn(false);
        when(request.getParam(0)).thenReturn(safeString("test"));

        TinyDBCommandWrapper wrapper = new TinyDBCommandWrapper(new TypeCommand());

        wrapper.execute(request, response);

        verify(response, times(0)).addSimpleStr(RESULT_OK);

        verify(response).addError(anyString());
    }

    private static class SomeCommand implements ITinyDBCommand {
        @Override
        public void execute(IDatabase db, IRequest request, IResponse response) {
            response.addSimpleStr(RESULT_OK);
        }
    }

    @ParamLength(2)
    private static class LengthCommand implements ITinyDBCommand {
        @Override
        public void execute(IDatabase db, IRequest request, IResponse response) {
            response.addSimpleStr(RESULT_OK);
        }
    }

    @ParamType(DataType.STRING)
    private static class TypeCommand implements ITinyDBCommand {
        @Override
        public void execute(IDatabase db, IRequest request, IResponse response) {
            response.addSimpleStr(RESULT_OK);
        }
    }
}
