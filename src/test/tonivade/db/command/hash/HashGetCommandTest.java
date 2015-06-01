package tonivade.db.command.hash;

import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.hash.HashGetCommand;
import tonivade.db.command.impl.CommandRule;
import tonivade.db.command.impl.CommandUnderTest;

@CommandUnderTest(HashGetCommand.class)
public class HashGetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData("a", hash(entry("key", "value")))
            .withParams("a", "key")
            .execute()
            .verify().addBulkStr("value");
    }

}
