package tonivade.db.command.impl;

import org.junit.Rule;
import org.junit.Test;

@Command(IncrementByCommand.class)
public class IncrementByCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("a", "10")
            .execute()
            .verify().addInt("10");

        rule.withParams("a", "10")
            .execute()
            .verify().addInt("20");
    }

}
