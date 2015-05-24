package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.IDatabase;

@RunWith(MockitoJUnitRunner.class)
public class RenameCommandTest {

    @Mock
    private IDatabase db;

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Test
    public void testExecute() {
        when(request.getParam(0)).thenReturn("a");
        when(request.getParam(1)).thenReturn("b");
        when(db.rename("a", "b")).thenReturn(true);

        RenameCommand command = new RenameCommand();

        command.execute(db, request, response);

        verify(response).addSimpleStr("OK");
    }

    @Test
    public void testExecuteError() {
        when(request.getParam(0)).thenReturn("a");
        when(request.getParam(1)).thenReturn("b");
        when(db.rename("a", "b")).thenReturn(false);

        RenameCommand command = new RenameCommand();

        command.execute(db, request, response);

        verify(response).addError("ERR no such key");
    }

}
