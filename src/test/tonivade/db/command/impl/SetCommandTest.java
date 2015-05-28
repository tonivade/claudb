package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

@CommandUnderTest(SetCommand.class)
public class SetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("a", "1")
            .execute()
            .assertThat("a", is(string("1")))
            .verify().addSimpleStr("OK");
    }

}
