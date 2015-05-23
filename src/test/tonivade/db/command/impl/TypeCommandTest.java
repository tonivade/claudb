package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.DatabaseValue;
import tonivade.db.data.IDatabase;

@RunWith(MockitoJUnitRunner.class)
public class TypeCommandTest {

    @Mock
    private IDatabase db;

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Test
    public void testExecute() {
        when(request.getParam(0)).thenReturn("a");
        when(db.get("a")).thenReturn(DatabaseValue.string("test"));

        TypeCommand command = new TypeCommand();

        command.execute(db, request, response);

        verify(response).addSimpleStr("string");
    }

}
