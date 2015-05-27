package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

public class DeleteCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.getDatabase().put("test", string("value"));

        rule.withParams("test").execute(new DeleteCommand());

        assertThat(rule.getDatabase().containsKey("test"), is(false));

        verify(rule.getResponse()).addInt(1);
    }

}
