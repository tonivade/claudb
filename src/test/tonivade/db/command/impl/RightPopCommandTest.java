package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static tonivade.db.data.DatabaseValue.list;

import org.junit.Rule;
import org.junit.Test;

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
