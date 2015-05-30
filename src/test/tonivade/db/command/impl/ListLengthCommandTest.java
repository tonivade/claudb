package tonivade.db.command.impl;

import static tonivade.db.data.DatabaseValue.list;

import org.junit.Rule;
import org.junit.Test;

@CommandUnderTest(ListLengthCommand.class)
public class ListLengthCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", list("a", "b", "c"))
            .withParams("key")
            .execute()
            .verify().addInt(3);
    }

}
