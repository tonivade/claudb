package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

@CommandUnderTest(FlushDBCommand.class)
public class FlushDBCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData("a", string("test"))
            .execute();

        assertThat(rule.getDatabase().isEmpty(), is(true));

        rule.verify().addSimpleStr("OK");
    }

}
