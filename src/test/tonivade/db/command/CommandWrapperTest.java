package tonivade.db.command;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import tonivade.db.data.IDatabase;

@RunWith(MockitoJUnitRunner.class)
public class CommandWrapperTest {

    @Spy
    private final SomeCommand command = new SomeCommand();

    @Mock
    private IDatabase db;

    @Mock
    private IRequest request;

    @Mock
    private IResponse response;

    @Test
    public void testExecute() {
        CommandWrapper wrapper = new CommandWrapper(command);

        wrapper.execute(db, request, response);

        verify(command).execute(db, request, response);
    }


    private class SomeCommand implements ICommand {
        @Override
        public void execute(IDatabase db, IRequest request, IResponse response) {

        }
    }

}
