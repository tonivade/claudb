package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.DataType;
import tonivade.db.data.DatabaseValue;
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
        when(request.getParams()).thenReturn(Arrays.asList("test"));
        when(db.remove("test")).thenReturn(new DatabaseValue(DataType.STRING));

        DeleteCommand command = new DeleteCommand();

        command.execute(db, request, response);

        verify(db).remove("test");

        verify(response).addInt(1);
    }

}
