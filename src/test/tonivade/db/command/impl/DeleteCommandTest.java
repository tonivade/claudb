package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.IDatabase;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCommandTest {

    @Mock
    private IDatabase db;

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Test
    public void testExecute() {
        Mockito.when(request.getParam(0)).thenReturn("test");

        DeleteCommand command = new DeleteCommand();

        command.execute(db, request, response);

        verify(db).remove("test");

        verify(response).addSimpleStr("OK");
    }

}
