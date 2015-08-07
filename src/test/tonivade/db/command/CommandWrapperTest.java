/*
 * Copyright (c) 2015, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */

package tonivade.db.command;

import static java.util.Collections.emptySet;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.redis.SafeString.safeString;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.annotation.ParamLength;
import tonivade.db.command.annotation.ParamType;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseKey;
import tonivade.db.data.IDatabase;

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

    @Before
    public void setUp() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getSubscriptions()).thenReturn(emptySet());
    }

    @Test
    public void testExecute() {
        CommandWrapper wrapper = new CommandWrapper(new SomeCommand());

        wrapper.execute(db, request, response);

        verify(response).addSimpleStr(ICommand.RESULT_OK);
    }

    @Test
    public void testLengthOK() {
        when(request.getLength()).thenReturn(3);

        CommandWrapper wrapper = new CommandWrapper(new LengthCommand());

        wrapper.execute(db, request, response);

        verify(response).addSimpleStr(ICommand.RESULT_OK);
    }

    @Test
    public void testLengthKO() {
        when(request.getLength()).thenReturn(1);

        CommandWrapper wrapper = new CommandWrapper(new LengthCommand());

        wrapper.execute(db, request, response);

        verify(response, times(0)).addSimpleStr(ICommand.RESULT_OK);

        verify(response).addError(anyString());
    }

    @Test
    public void testTypeOK() {
        when(db.isType(any(DatabaseKey.class), eq(DataType.STRING))).thenReturn(true);
        when(request.getParam(0)).thenReturn(safeString("test"));

        CommandWrapper wrapper = new CommandWrapper(new TypeCommand());

        wrapper.execute(db, request, response);

        verify(response).addSimpleStr(ICommand.RESULT_OK);
    }

    @Test
    public void testTypeKO() {
        when(db.isType(any(DatabaseKey.class), eq(DataType.STRING))).thenReturn(false);
        when(request.getParam(0)).thenReturn(safeString("test"));

        CommandWrapper wrapper = new CommandWrapper(new TypeCommand());

        wrapper.execute(db, request, response);

        verify(response, times(0)).addSimpleStr(ICommand.RESULT_OK);

        verify(response).addError(anyString());
    }

    private static class SomeCommand implements ICommand {
        @Override
        public void execute(IDatabase db, IRequest request, IResponse response) {
            response.addSimpleStr(RESULT_OK);
        }
    }

    @ParamLength(2)
    private static class LengthCommand implements ICommand {
        @Override
        public void execute(IDatabase db, IRequest request, IResponse response) {
            response.addSimpleStr(RESULT_OK);
        }
    }

    @ParamType(DataType.STRING)
    private static class TypeCommand implements ICommand {
        @Override
        public void execute(IDatabase db, IRequest request, IResponse response) {
            response.addSimpleStr(RESULT_OK);
        }
    }

}
