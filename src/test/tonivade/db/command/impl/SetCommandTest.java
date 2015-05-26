package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

public class SetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        when(rule.getRequest().getParam(0)).thenReturn("a");
        when(rule.getRequest().getParam(1)).thenReturn("1");

        rule.execute(new SetCommand());

        assertThat(rule.getDatabase().get("a"), is(string("1")));

        verify(rule.getResponse()).addSimpleStr("OK");
    }

}
