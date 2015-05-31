package tonivade.db.command.impl;

import static tonivade.db.data.DatabaseValue.set;

import org.junit.Rule;
import org.junit.Test;

@CommandUnderTest(SetCardinalityCommand.class)
public class SetCardinalityCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", set("a", "b", "c"))
            .withParams("key")
            .execute()
            .verify().addInt(3);

        rule.withParams("notExists")
            .execute()
            .verify().addInt(0);
    }

}
