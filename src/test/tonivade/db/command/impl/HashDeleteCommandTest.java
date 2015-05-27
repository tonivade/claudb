package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

public class HashDeleteCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.getDatabase().put("key", hash(entry("a", "1")));

        rule.withParams("key", "a", "b", "c").execute(new HashDeleteCommand());

        verify(rule.getResponse()).addInt(true);
    }

    @Test
    public void testExecuteNoKeys() throws Exception {
        rule.getDatabase().put("key", hash(entry("d", "1")));

        rule.withParams("key", "a", "b", "c").execute(new HashDeleteCommand());

        verify(rule.getResponse()).addInt(false);
    }

}
