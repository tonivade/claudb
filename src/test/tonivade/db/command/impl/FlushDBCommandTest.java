package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.data.DatabaseValue;

public class FlushDBCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.getDatabase().put("a", DatabaseValue.string("test"));

        rule.execute(new FlushDBCommand());

        assertThat(rule.getDatabase().isEmpty(), is(true));

        verify(rule.getResponse()).addSimpleStr("OK");
    }

}
