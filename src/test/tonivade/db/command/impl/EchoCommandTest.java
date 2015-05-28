package tonivade.db.command.impl;

import org.junit.Rule;
import org.junit.Test;

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
