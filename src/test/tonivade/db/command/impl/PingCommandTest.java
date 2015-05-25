package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.command.IRequest;
import tonivade.db.command.IResponse;

@RunWith(MockitoJUnitRunner.class)
public class PingCommandTest {

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Test
    public void testExecute() {
        PingCommand command = new PingCommand();

        command.execute(null, request, response);

        verify(response).addSimpleStr("PONG");
    }

}
