package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;

public class PingCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.execute(new PingCommand());

        verify(rule.getResponse()).addSimpleStr("PONG");
    }

    @Test
    public void testExecuteWithParam() {
        rule.withParams("Hi!").execute(new PingCommand());

        verify(rule.getResponse()).addBulkStr("Hi!");
    }

}
