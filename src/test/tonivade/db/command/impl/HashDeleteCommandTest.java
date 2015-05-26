package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;

public class HashDeleteCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        when(rule.getRequest().getParam(0)).thenReturn("key");
        when(rule.getRequest().getParams()).thenReturn(Arrays.asList("key", "a", "b", "c"));

        rule.getDatabase().put("key", hash(entry("a", "1")));

        rule.execute(new HashDeleteCommand());

        verify(rule.getResponse()).addInt(true);
    }

    @Test
    public void testExecuteNoKeys() throws Exception {
        when(rule.getRequest().getParam(0)).thenReturn("key");
        when(rule.getRequest().getParams()).thenReturn(Arrays.asList("key", "a", "b", "c"));

        rule.getDatabase().put("key", hash(entry("d", "1")));

        rule.execute(new HashDeleteCommand());

        verify(rule.getResponse()).addInt(false);
    }

}
