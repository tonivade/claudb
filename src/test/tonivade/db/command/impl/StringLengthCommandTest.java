package tonivade.db.command.impl;

import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

@CommandUnderTest(StringLengthCommand.class)
public class StringLengthCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData("a", string("test"))
            .withParams("a")
            .execute()
            .verify().addInt(4);
    }

}
