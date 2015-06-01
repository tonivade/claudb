package tonivade.db.command.string;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.impl.CommandRule;
import tonivade.db.command.impl.CommandUnderTest;
import tonivade.db.command.string.IncrementByCommand;

@CommandUnderTest(IncrementByCommand.class)
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

        rule.withParams("a", "5")
            .execute()
            .verify().addInt("25");
    }

}
