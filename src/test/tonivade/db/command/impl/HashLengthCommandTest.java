package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

public class HashLengthCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.getDatabase().put("key", hash(entry("a", "1"), entry("b", "2")));

        rule.withParams("key", "a").execute(new HashLengthCommand());

        verify(rule.getResponse()).addInt(2);

    }

}
