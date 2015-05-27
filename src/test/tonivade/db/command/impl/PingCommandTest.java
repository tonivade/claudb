package tonivade.db.command.impl;

import org.junit.Rule;
import org.junit.Test;

@Command(PingCommand.class)
public class PingCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.execute()
            .verify().addSimpleStr("PONG");
    }

    @Test
    public void testExecuteWithParam() {
        rule.withParams("Hi!")
            .execute()
            .verify().addBulkStr("Hi!");
    }

}
