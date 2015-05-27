package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;

public class IncrementByCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("a", "10").execute(new IncrementByCommand());

        verify(rule.getResponse()).addInt("10");

        rule.withParams("a", "10").execute(new IncrementByCommand());

        verify(rule.getResponse()).addInt("20");
    }

}
