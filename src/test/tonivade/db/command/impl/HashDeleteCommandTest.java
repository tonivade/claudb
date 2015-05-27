package tonivade.db.command.impl;

import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

@Command(HashDeleteCommand.class)
public class HashDeleteCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", hash(entry("a", "1")))
            .withParams("key", "a", "b", "c")
            .execute()
            .verify().addInt(true);
    }

    @Test
    public void testExecuteNoKeys() throws Exception {
        rule.withData("key", hash(entry("d", "1")))
            .withParams("key", "a", "b", "c")
            .execute()
            .verify().addInt(false);
    }

}
