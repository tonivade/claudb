package tonivade.db.command.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
public class HashExistsCommandTest {

    @Mock
    private IDatabase db;

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Test
    public void testExecute() throws Exception {
        when(request.getParam(0)).thenReturn("key");
        when(request.getParam(1)).thenReturn("a");

        when(db.getOrDefault(eq("key"), any())).thenReturn(hash(entry("a", "1")));

        HashExistsCommand command = new HashExistsCommand();

        command.execute(db, request, response);

        verify(response).addInt(true);
    }

}
