package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

public class HashSetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.getDatabase().put("a", hash(entry("key", "value")));

        rule.withParams("a", "key", "value").execute(new HashSetCommand());

        verify(rule.getResponse()).addInt(false);
    }

}
