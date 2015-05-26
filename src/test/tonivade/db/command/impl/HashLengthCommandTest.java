package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

public class HashLengthCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        when(rule.getRequest().getParam(0)).thenReturn("key");
        when(rule.getRequest().getParam(1)).thenReturn("a");

        rule.getDatabase().put("key", hash(entry("a", "1"), entry("b", "2")));

        rule.execute(new HashLengthCommand());

        verify(rule.getResponse()).addInt(2);

    }

}
