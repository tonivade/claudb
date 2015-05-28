package tonivade.db.command.impl;

import org.junit.Rule;
import org.junit.Test;

@CommandUnderTest(DecrementByCommand.class)
public class DecrementByCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("a", "10")
            .execute()
            .verify().addInt("-10");

        rule.withParams("a", "10")
            .execute()
            .verify().addInt("-20");

        rule.withParams("a", "5")
            .execute()
            .verify().addInt("-25");
    }

}
