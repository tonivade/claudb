package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

public class RenameCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        when(rule.getRequest().getParam(0)).thenReturn("a");
        when(rule.getRequest().getParam(1)).thenReturn("b");

        rule.getDatabase().put("a", string("1"));

        rule.execute(new RenameCommand());

        assertThat(rule.getDatabase().get("a"), is(nullValue()));
        assertThat(rule.getDatabase().get("b"), is(string("1")));

        verify(rule.getResponse()).addSimpleStr("OK");
    }

    @Test
    public void testExecuteError() {
        when(rule.getRequest().getParam(0)).thenReturn("a");
        when(rule.getRequest().getParam(1)).thenReturn("b");

        rule.execute(new RenameCommand());

        assertThat(rule.getDatabase().get("a"), is(nullValue()));
        assertThat(rule.getDatabase().get("b"), is(nullValue()));

        verify(rule.getResponse()).addError("ERR no such key");
    }

}
