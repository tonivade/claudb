package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.IDatabase;

@RunWith(MockitoJUnitRunner.class)
public class HashGetCommandTest {

    @Mock
    private IDatabase db;

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Test
    public void testExecute() {
        when(request.getParam(0)).thenReturn("a");
        when(request.getParam(1)).thenReturn("key");
        when(db.get("a")).thenReturn(hash(entry("key", "value")));

        HashGetCommand command = new HashGetCommand();

        command.execute(db, request, response);

        verify(response).addBulkStr("value");
    }

}
