package tonivade.db.command.impl;

import org.junit.Rule;
import org.junit.Test;

@CommandUnderTest(DecrementCommand.class)
public class DecrementCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("a")
            .execute()
            .verify().addInt("-1");

        rule.withParams("a")
            .execute()
            .verify().addInt("-2");

        rule.withParams("a")
            .execute()
            .verify().addInt("-3");
    }

}
