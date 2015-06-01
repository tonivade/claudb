package tonivade.db.command.list;

import static org.hamcrest.CoreMatchers.is;
import static tonivade.db.data.DatabaseValue.list;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.impl.CommandRule;
import tonivade.db.command.impl.CommandUnderTest;
import tonivade.db.command.list.LeftPopCommand;

@CommandUnderTest(LeftPopCommand.class)
public class LeftPopCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", list("a", "b", "c"))
            .withParams("key")
            .execute()
            .assertThat("key", is(list("b", "c")))
            .verify().addBulkStr("a");
    }

}
