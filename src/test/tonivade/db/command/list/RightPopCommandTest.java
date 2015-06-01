package tonivade.db.command.list;

import static org.hamcrest.CoreMatchers.is;
import static tonivade.db.data.DatabaseValue.list;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.impl.CommandRule;
import tonivade.db.command.impl.CommandUnderTest;
import tonivade.db.command.list.RightPopCommand;

@CommandUnderTest(RightPopCommand.class)
public class RightPopCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", list("a", "b", "c"))
            .withParams("key")
            .execute()
            .assertThat("key", is(list("a", "b")))
            .verify().addBulkStr("c");
    }

}
