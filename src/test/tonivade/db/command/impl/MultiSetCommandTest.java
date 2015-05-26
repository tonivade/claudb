package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;

public class MultiSetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        when(rule.getRequest().getParams()).thenReturn(
                Arrays.asList("a", "1", "b", "2", "c", "3"));

        rule.execute(new MultiSetCommand());

        assertThat(rule.getDatabase().get("a"), is(string("1")));
        assertThat(rule.getDatabase().get("b"), is(string("2")));
        assertThat(rule.getDatabase().get("c"), is(string("3")));
        assertThat(rule.getDatabase().size(), is(3));

        verify(rule.getResponse()).addSimpleStr("OK");
    }

}
