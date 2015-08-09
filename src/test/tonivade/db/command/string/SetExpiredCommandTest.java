package tonivade.db.command.string;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static tonivade.db.DatabaseKeyMatchers.isExpired;
import static tonivade.db.DatabaseKeyMatchers.isNotExpired;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(SetExpiredCommand.class)
public class SetExpiredCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withParams("a", "1", "1")
            .execute()
            .assertValue("a", is(string("1")))
            .verify().addSimpleStr("OK");

        Thread.sleep(500);
        rule.assertKey("a", isNotExpired())
            .assertValue("a", notNullValue());

        Thread.sleep(500);
        rule.assertKey("a", isExpired())
            .assertValue("a", nullValue());
    }

}
