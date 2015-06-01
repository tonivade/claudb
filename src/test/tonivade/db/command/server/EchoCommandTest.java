package tonivade.db.command.server;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.impl.CommandRule;
import tonivade.db.command.impl.CommandUnderTest;
import tonivade.db.command.server.EchoCommand;

@CommandUnderTest(EchoCommand.class)
public class EchoCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("test")
            .execute()
            .verify().addBulkStr("test");
    }

}
