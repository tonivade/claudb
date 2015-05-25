package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;

@RunWith(MockitoJUnitRunner.class)
public class EchoCommandTest {

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Test
    public void testExecute() {
        when(request.getParam(0)).thenReturn("test");

        EchoCommand command = new EchoCommand();

        command.execute(null, request, response);

        verify(response).addBulkStr("test");
    }

}
