package tonivade.db.command.string;

import static org.hamcrest.CoreMatchers.is;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.data.DatabaseKey;

@CommandUnderTest(SetExpiredCommand.class)
public class SetExpiredCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withParams("a", "10", "1")
            .execute()
            .assertThat("a", is(string("1")))
            .verify().addSimpleStr("OK");
        rule.getDatabase().get(DatabaseKey.safeKey("a"));
    }

}
