package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

public class AppendCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.getDatabase().put("test", string("Hola"));

        rule.withParams("test", " mundo").execute(new AppendCommand());

        verify(rule.getResponse()).addInt(10);
    }

    @Test
    public void testExecuteNoExists() {
        rule.withParams("test", " mundo").execute(new AppendCommand());

        verify(rule.getResponse()).addInt(6);
    }

}
