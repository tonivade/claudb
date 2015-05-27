package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

public class HashExistsCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.getDatabase().put("key", hash(entry("a", "1")));

        rule.withParams("key", "a").execute(new HashExistsCommand());

        verify(rule.getResponse()).addInt(true);
    }

}
