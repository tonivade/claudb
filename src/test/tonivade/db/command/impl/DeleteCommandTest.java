package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.data.DatabaseValue;

public class DeleteCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.getDatabase().put("test", DatabaseValue.string("value"));
        when(rule.getRequest().getParams()).thenReturn(Arrays.asList("test"));

        rule.execute(new DeleteCommand());

        assertThat(rule.getDatabase().containsKey("test"), is(false));

        verify(rule.getResponse()).addInt(1);
    }

}
