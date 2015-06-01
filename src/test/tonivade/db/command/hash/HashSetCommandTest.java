package tonivade.db.command.hash;

import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.command.hash.HashSetCommand;
import tonivade.db.command.impl.CommandRule;
import tonivade.db.command.impl.CommandUnderTest;

@CommandUnderTest(HashSetCommand.class)
public class HashSetCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecute() {
        rule.withData("a", hash(entry("key", "value")))
            .withParams("a", "key", "value")
            .execute()
            .verify().addInt(false);
    }

}
