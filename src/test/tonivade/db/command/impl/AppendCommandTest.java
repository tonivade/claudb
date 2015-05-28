package tonivade.db.command.impl;

import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

@CommandUnderTest(AppendCommand.class)
public class AppendCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData("test", string("Hola"))
            .withParams("test", " mundo").execute()
            .verify().addInt(10);
    }

    @Test
    public void testExecuteNoExists() {
        rule.withParams("test", " mundo")
            .execute()
            .verify().addInt(6);
    }

}
