package tonivade.db.command.bitset;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;
import tonivade.db.data.DatabaseValue;

@CommandUnderTest(BitCountCommand.class)
public class BitCountCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("test", DatabaseValue.bitset(1, 5, 10, 15))
            .withParams("test")
            .execute()
            .verify().addInt(4);
    }

}
