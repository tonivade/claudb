package tonivade.db.command.impl;

import static org.mockito.Mockito.verify;
import static tonivade.db.data.DatabaseValue.entry;
import static tonivade.db.data.DatabaseValue.hash;
import static tonivade.db.data.DatabaseValue.string;

import org.junit.Rule;
import org.junit.Test;

public class TypeCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void testExecuteString() {
        rule.getDatabase().put("a", string("string"));

        rule.withParams("a").execute(new TypeCommand());

        verify(rule.getResponse()).addSimpleStr("string");
    }

    @Test
    public void testExecuteHash() {
        rule.getDatabase().put("a", hash(entry("k1", "v1")));

        rule.withParams("a").execute(new TypeCommand());

        verify(rule.getResponse()).addSimpleStr("hash");
    }

}
