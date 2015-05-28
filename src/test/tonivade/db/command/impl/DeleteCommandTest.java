package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

@CommandUnderTest(DeleteCommand.class)
public class DeleteCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData("test", string("value"))
            .withParams("test")
            .execute()
            .assertThat("test", is(nullValue()))
            .verify().addInt(1);
    }

}
