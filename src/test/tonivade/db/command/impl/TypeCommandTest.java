package tonivade.db.command.impl;

import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

@CommandUnderTest(TypeCommand.class)
public class TypeCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecuteString() {
        rule.withData("a", string("string"))
            .withParams("a").execute()
            .verify().addSimpleStr("string");
    }

    @Test
    public void testExecuteHash() {
        rule.withData("a", hash(entry("k1", "v1")))
            .withParams("a")
            .execute()
            .verify().addSimpleStr("hash");
    }

    @Test
    public void testExecuteNotExists() {
        rule.withData("a", string("string"))
            .withParams("b").execute()
            .verify().addSimpleStr("none");
    }

}
