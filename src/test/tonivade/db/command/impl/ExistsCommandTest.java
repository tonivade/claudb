package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

public class ExistsCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.getDatabase().put("test", string("value"));

        rule.withParams("test").execute(new ExistsCommand());

        verify(rule.getResponse()).addInt(true);
    }

}
