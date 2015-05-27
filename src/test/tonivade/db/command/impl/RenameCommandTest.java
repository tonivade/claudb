package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

@Command(RenameCommand.class)
public class RenameCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData("a", string("1"))
            .withParams("a", "b")
            .execute()
            .assertThat("a", is(nullValue()))
            .assertThat("b", is(string("1")))
            .verify().addSimpleStr("OK");
    }

    @Test
    public void testExecuteError() {
        rule.withParams("a", "b")
            .execute()
            .assertThat("a", is(nullValue()))
            .assertThat("b", is(nullValue()))
            .verify().addError("ERR no such key");
    }

}
