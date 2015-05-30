package tonivade.db.command.impl;

import static tonivade.db.data.DatabaseValue.set;

import org.junit.Rule;
import org.junit.Test;

@CommandUnderTest(SetMembersCommand.class)
public class SetMembersCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", set("a", "b", "c"))
            .withParams("key")
            .execute()
            .verify().addValue(set("a", "b", "c"));
    }
}
