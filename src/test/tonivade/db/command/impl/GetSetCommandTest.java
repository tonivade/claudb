package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import tonivade.db.data.DatabaseValue;

@Command(GetSetCommand.class)
public class GetSetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Captor
    private ArgumentCaptor<DatabaseValue> captor;

    @Test
    public void testExecute() {
        rule.withData("a", string("1"))
            .withParams("a", "2")
            .execute()
            .verify().addValue(captor.capture());

        DatabaseValue value = captor.getValue();

        assertThat(value.getValue(), is("1"));
    }

}
