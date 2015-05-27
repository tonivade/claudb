package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

public class StringLengthCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.getDatabase().put("a", string("test"));

        rule.withParams("a").execute(new StringLengthCommand());

        verify(rule.getResponse()).addInt(4);
    }

}
