package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

public class StringLengthCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        when(rule.getRequest().getParam(0)).thenReturn("a");

        rule.getDatabase().put("a", string("test"));

        StringLengthCommand command = new StringLengthCommand();

        command.execute(rule.getDatabase(), rule.getRequest(), rule.getResponse());

        verify(rule.getResponse()).addInt(4);
    }

}
