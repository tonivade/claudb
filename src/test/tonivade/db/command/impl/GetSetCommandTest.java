package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.data.DatabaseValue;

public class GetSetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<DatabaseValue> captor;

    @Test
    public void testExecute() {
        rule.getDatabase().put("a", string("1"));

        rule.withParams("a", "2").execute(new GetSetCommand());

        verify(rule.getResponse()).addValue(captor.capture());

        DatabaseValue value = captor.getValue();

        assertThat(value.getValue(), is("1"));
    }

}
