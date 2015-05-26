package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

public class HashGetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        when(rule.getRequest().getParam(0)).thenReturn("a");
        when(rule.getRequest().getParam(1)).thenReturn("key");

        rule.getDatabase().put("a", hash(entry("key", "value")));

        rule.execute(new HashGetCommand());

        verify(rule.getResponse()).addBulkStr("value");
    }

}
