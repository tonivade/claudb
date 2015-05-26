package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;

public class IncrementByCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        when(rule.getRequest().getParam(0)).thenReturn("a");
        when(rule.getRequest().getParam(1)).thenReturn("10");

        rule.execute(new IncrementByCommand());

        verify(rule.getResponse()).addInt("10");

        rule.execute(new IncrementByCommand());

        verify(rule.getResponse()).addInt("20");
    }

}
