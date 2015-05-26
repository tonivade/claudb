package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

public class AppendCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.getDatabase().put("test", string("Hola"));

        when(rule.getRequest().getParam(0)).thenReturn("test");
        when(rule.getRequest().getParam(1)).thenReturn(" mundo");

        rule.execute(new AppendCommand());

        verify(rule.getResponse()).addInt(10);
    }

}
