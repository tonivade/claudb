package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;

public class DecrementCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withParams("a").execute(new DecrementCommand());

        verify(rule.getResponse()).addInt("-1");

        rule.execute(new DecrementCommand());

        verify(rule.getResponse()).addInt("-2");
    }

}
