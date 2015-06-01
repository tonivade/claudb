package tonivade.db.command.hash;

import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.hash.HashExistsCommand;
import tonivade.db.command.impl.CommandRule;
import tonivade.db.command.impl.CommandUnderTest;

@CommandUnderTest(HashExistsCommand.class)
public class HashExistsCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() throws Exception {
        rule.withData("key", hash(entry("a", "1")))
            .withParams("key", "a")
            .execute()
            .verify().addInt(true);
    }

}
