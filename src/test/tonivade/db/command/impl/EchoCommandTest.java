package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;

public class EchoCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("test").execute(new EchoCommand());

        verify(rule.getResponse()).addBulkStr("test");
    }

}
