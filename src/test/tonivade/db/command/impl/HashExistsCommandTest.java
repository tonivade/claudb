package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

public class HashExistsCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        when(rule.getRequest().getParam(0)).thenReturn("key");
        when(rule.getRequest().getParam(1)).thenReturn("a");

        rule.getDatabase().put("key", hash(entry("a", "1")));

        rule.execute(new HashExistsCommand());

        verify(rule.getResponse()).addInt(true);
    }

}
