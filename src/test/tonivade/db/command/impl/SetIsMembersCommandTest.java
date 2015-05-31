package tonivade.db.command.impl;

import static tonivade.db.data.DatabaseValue.set;

import org.junit.Rule;
import org.junit.Test;

@CommandUnderTest(SetIsMemberCommand.class)
public class SetIsMembersCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", set("a", "b", "c"))
            .withParams("key", "a")
            .execute()
            .verify().addInt(true);

        rule.withData("key", set("a", "b", "c"))
            .withParams("key", "z")
            .execute()
            .verify().addInt(false);
    }

}
