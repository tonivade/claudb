package tonivade.db.command.set;

import static tonivade.db.data.DatabaseValue.set;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.impl.CommandRule;
import tonivade.db.command.impl.CommandUnderTest;
import tonivade.db.command.set.SetIsMemberCommand;

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
