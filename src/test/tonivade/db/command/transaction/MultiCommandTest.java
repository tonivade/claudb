package tonivade.db.command.transaction;

import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;

import tonivade.db.TransactionState;
import tonivade.db.command.CommandRule;
import tonivade.db.command.CommandUnderTest;

@CommandUnderTest(MultiCommand.class)
public class MultiCommandTest {

    @Rule
    public final CommandRule rule = new CommandRule(this);

    @Test
    public void executeWithoutActiveTransaction() throws Exception {
        rule.execute()
            .verify().addSimpleStr("OK");
    }

    @Test
    public void executeWithActiveTransaction() throws Exception {
        when(rule.getSession().getValue("tx")).thenReturn(new TransactionState());

        rule.execute()
            .verify().addError("ERR MULTI calls can not be nested");
    }
}
