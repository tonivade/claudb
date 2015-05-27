package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

public class RenameCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.getDatabase().put("a", string("1"));

        rule.withParams("a", "b").execute(new RenameCommand());

        assertThat(rule.getDatabase().get("a"), is(nullValue()));
        assertThat(rule.getDatabase().get("b"), is(string("1")));

        verify(rule.getResponse()).addSimpleStr("OK");
    }

    @Test
    public void testExecuteError() {
        rule.withParams("a", "b").execute(new RenameCommand());

        assertThat(rule.getDatabase().get("a"), is(nullValue()));
        assertThat(rule.getDatabase().get("b"), is(nullValue()));

        verify(rule.getResponse()).addError("ERR no such key");
    }

}
