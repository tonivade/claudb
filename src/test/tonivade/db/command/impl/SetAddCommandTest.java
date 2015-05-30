package tonivade.db.command.impl;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;

import tonivade.db.data.DatabaseValue;

@CommandUnderTest(SetAddCommand.class)
public class SetAddCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withParams("key", "value")
            .execute()
            .assertThat("key", CoreMatchers.is(DatabaseValue.set("value")))
            .verify().addInt(1);
    }

}
