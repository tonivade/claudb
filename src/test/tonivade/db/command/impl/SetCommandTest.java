package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

public class SetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("a", "1").execute(new SetCommand());

        assertThat(rule.getDatabase().get("a"), is(string("1")));

        verify(rule.getResponse()).addSimpleStr("OK");
    }

}
