package tonivade.db.command.impl;

import static org.hamcrest.CoreMatchers.is;
import static tonivade.db.data.DatabaseValue.list;

import org.junit.Rule;
import org.junit.Test;

@CommandUnderTest(RightPushCommand.class)
public class RightPushCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withParams("key", "a", "b", "c")
            .execute()
            .assertThat("key", is(list("a", "b", "c")))
            .verify().addInt(3);

        rule.withParams("key", "d")
            .execute()
            .assertThat("key", is(list("a", "b", "c", "d")))
            .verify().addInt(4);
    }

}
