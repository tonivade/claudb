package tonivade.db.command.bitset;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.data.DatabaseValue;

@CommandUnderTest(SetBitCommand.class)
public class SetBitCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecuteOne() throws Exception {
        rule.withData("test", DatabaseValue.bitset())
            .withParams("test", "10", "1")
            .execute()
            .verify().addInt(false);
    }

    @Test
    public void testExecuteZero() throws Exception {
        rule.withData("test", DatabaseValue.bitset(10))
            .withParams("test", "10", "0")
            .execute()
            .verify().addInt(true);
    }
}
