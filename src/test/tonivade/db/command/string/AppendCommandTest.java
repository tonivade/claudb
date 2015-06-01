package tonivade.db.command.string;

import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.impl.CommandRule;
import tonivade.db.command.impl.CommandUnderTest;
import tonivade.db.command.string.AppendCommand;

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
