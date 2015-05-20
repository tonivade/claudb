package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;
import tonivade.db.data.IDatabase;

@RunWith(MockitoJUnitRunner.class)
public class IncrementByCommandTest {

    @Mock
    private IDatabase db;

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Test
    public void testExecute() {
        IncrementByCommand command = new IncrementByCommand();

        command.execute(db, request, response);

        verify(response).addSimpleStr("PONG");
    }

}
